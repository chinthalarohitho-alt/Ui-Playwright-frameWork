package hooks;

import Initialization.FrameWorkInitialization;
import config.ConfigReader;
import config.Settings;
import io.cucumber.java.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cucumber hooks with clean, minimal logging.
 */
public class Hooks {

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    private FrameWorkInitialization FM;

    // Tracking
    private static long suiteStartTime;
    private long scenarioStartTime;
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    // Separator configuration
    private static final int SEPARATOR_LENGTH = 120;
    private static final String SEPARATOR = "=".repeat(SEPARATOR_LENGTH);

    // Directories
    private static final String SCREENSHOTS_DIR = "target/screenshots";

    // Cache for feature tags (URI -> Set of tags)
    private static final Map<String, Set<String>> featureTagsCache = new HashMap<>();

    // ==================== BEFORE ALL ====================

    @BeforeAll
    public static void beforeAll() throws IOException {
        suiteStartTime = System.currentTimeMillis();
        Properties props = new Properties();
        System.out.println("\n" + SEPARATOR);
        System.out.println("TEST SUITE STARTED");
        System.out.println(SEPARATOR);

        // Load configuration
        try {
            props.load(new FileInputStream("src/main/java/config/BrowserConfig.properties"));
            ConfigReader.PopulateSettings();
            System.out.println("Environment: " + System.getProperty("env") + " | Browser: " + props.getProperty("BrowserName") + " | Headless: " + props.getProperty("Headless_status") + " | BaseUrl: " + Settings.Url);
        } catch (Exception e) {
            logger.error("Configuration loading failed: {}", e.getMessage());
            throw new RuntimeException("Setup failed", e);
        }

        // Create directories
        createDirectories();
        System.out.println(SEPARATOR + "\n");
    }

    // ==================== BEFORE SCENARIO ====================

    @Before
    public void setup(Scenario scenario) throws IOException {
        scenarioStartTime = System.currentTimeMillis();
        totalTests++;

        System.out.println("\n▶ Starting: " + scenario.getName());

        // Get only scenario-specific tags (exclude feature tags)
        Collection<String> scenarioTags = getScenarioOnlyTags(scenario);
        if (!scenarioTags.isEmpty()) {
            System.out.println("  Tags: " + scenarioTags);
        }

        try {
            FM = new FrameWorkInitialization();
            FM.setUp();
        } catch (Exception e) {
            System.out.println("✗ Setup failed: " + e.getMessage());
            captureScreenshotOnError(scenario, "setup_failed");
            throw e;
        }
    }

    // ==================== AFTER SCENARIO ====================

    @After
    public void teardown(Scenario scenario) {
        long duration = System.currentTimeMillis() - scenarioStartTime;

        try {
            // Handle result
            if (scenario.isFailed()) {
                failedTests++;
                System.out.println("✗ FAILED: " + scenario.getName() + " (" + formatTime(duration) + ")");
                captureFailureArtifacts(scenario);
            } else {
                passedTests++;
                System.out.println("✓ PASSED: " + scenario.getName() + " (" + formatTime(duration) + ")");
            }

            // Save trace on failure
            if (scenario.isFailed() && FM != null && FM.getContext() != null) {
                String enableTracing = FM.getProperties().getProperty("enable_tracing", "false");
                if ("true".equalsIgnoreCase(enableTracing)) {
                    String tracePath = "target/traces/" + sanitize(scenario.getName()) + ".zip";
                    FM.getContext().tracing().stop(new com.microsoft.playwright.Tracing.StopOptions()
                            .setPath(java.nio.file.Paths.get(tracePath)));
                    System.out.println("  Trace saved: " + tracePath);
                }
            }
        } catch (Exception e) {
            logger.error("Teardown error: {}", e.getMessage());
        } finally {
            cleanup();
        }
    }

    // ==================== AFTER ALL ====================

    @AfterAll
    public static void afterAll() {
        long totalTime = System.currentTimeMillis() - suiteStartTime;

        System.out.println("\n" + SEPARATOR);
        System.out.println("TEST SUITE COMPLETED");
        System.out.println(SEPARATOR);
        System.out.println("Total: " + totalTests + " | Passed: " + passedTests + " | Failed: " + failedTests);
        System.out.println("Duration: " + formatTime(totalTime));
        System.out.println("Pass Rate: " + (totalTests > 0 ? (passedTests * 100 / totalTests) : 0) + "%");
        System.out.println(SEPARATOR + "\n");

        // Final cleanup
        try {
            new FrameWorkInitialization().shutdownAll();
        } catch (Exception e) {
            logger.warn("Cleanup warning: {}", e.getMessage());
        }

        // Generate reports
        generateReports();
    }

    // ==================== DYNAMIC TAG FILTERING ====================

    /**
     * Gets only scenario-specific tags by reading the feature file dynamically.
     * Excludes tags that appear at the Feature level.
     */
    private Collection<String> getScenarioOnlyTags(Scenario scenario) {
        Collection<String> allTags = scenario.getSourceTagNames();

        if (allTags.isEmpty()) {
            return allTags;
        }

        try {
            // Get feature file URI
            String featureUri = scenario.getUri().toString();

            // Get feature-level tags from the file
            Set<String> featureTags = getFeatureLevelTags(featureUri);

            // Filter out feature-level tags
            return allTags.stream()
                    .filter(tag -> !featureTags.contains(tag))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.debug("Could not filter tags, showing all: {}", e.getMessage());
            return allTags; // Fallback: show all tags
        }
    }

    /**
     * Extracts feature-level tags by parsing the feature file.
     * Uses caching to avoid repeated file reads.
     */
    private Set<String> getFeatureLevelTags(String featureUri) {
        // Check cache first
        if (featureTagsCache.containsKey(featureUri)) {
            return featureTagsCache.get(featureUri);
        }

        Set<String> featureTags = new HashSet<>();

        try {
            // Convert URI to file path
            Path featureFile = getFeatureFilePath(featureUri);

            if (featureFile == null || !Files.exists(featureFile)) {
                logger.debug("Feature file not found: {}", featureUri);
                return featureTags;
            }

            // Read and parse feature file
            List<String> lines = Files.readAllLines(featureFile);

            for (String line : lines) {
                String trimmed = line.trim();

                // Stop when we hit the Feature: keyword
                if (trimmed.startsWith("Feature:")) {
                    break;
                }

                // Collect tags before Feature: keyword (these are feature-level tags)
                if (trimmed.startsWith("@")) {
                    // Split multiple tags on the same line: @tag1 @tag2 @tag3
                    String[] tags = trimmed.split("\\s+");
                    for (String tag : tags) {
                        if (tag.startsWith("@")) {
                            featureTags.add(tag);
                        }
                    }
                }
            }

            // Cache the result
            featureTagsCache.put(featureUri, featureTags);
            logger.debug("Feature tags for {}: {}", featureUri, featureTags);

        } catch (Exception e) {
            logger.debug("Could not read feature file: {}", e.getMessage());
        }

        return featureTags;
    }

    /**
     * Converts feature URI to actual file path.
     * Handles different URI formats (file:, classpath:, etc.)
     */
    private Path getFeatureFilePath(String featureUri) {
        try {
            // Remove URI prefixes
            String filePath = featureUri
                    .replace("file:", "")
                    .replace("classpath:", "");

            // Try direct path first
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return path;
            }

            // Try src/test/resources path
            path = Paths.get("src/test/resources", filePath);
            if (Files.exists(path)) {
                return path;
            }

            // Try relative to project root
            path = Paths.get(System.getProperty("user.dir"), filePath);
            if (Files.exists(path)) {
                return path;
            }

            logger.debug("Could not find feature file for URI: {}", featureUri);
            return null;

        } catch (Exception e) {
            logger.debug("Error resolving feature file path: {}", e.getMessage());
            return null;
        }
    }

    // ==================== HELPERS ====================

    /**
     * Captures screenshot and page info on failure.
     */
    private void captureFailureArtifacts(Scenario scenario) {
        if (FM == null || FM.getPage() == null) {
            System.out.println("  Cannot capture screenshot - page not available");
            return;
        }

        try {
            // Screenshot
            byte[] screenshot = FM.getPage().screenshot();
            String fileName = sanitize(scenario.getName()) + "_" + timestamp() + ".png";
            Path path = Paths.get(SCREENSHOTS_DIR, fileName);
            Files.write(path, screenshot);
            scenario.attach(screenshot, "image/png", "Failed Screenshot");
            System.out.println("  Screenshot: " + fileName);

            // Current URL
            String url = FM.getPage().url();
            System.out.println("  URL: " + url);

        } catch (Exception e) {
            System.out.println("  Screenshot failed: " + e.getMessage());
        }
    }

    /**
     * Captures screenshot on setup error.
     */
    private void captureScreenshotOnError(Scenario scenario, String prefix) {
        try {
            if (FM != null && FM.getPage() != null) {
                byte[] screenshot = FM.getPage().screenshot();
                scenario.attach(screenshot, "image/png", prefix + "_" + scenario.getName());
                System.out.println("  Screenshot captured: " + prefix);
            }
        } catch (Exception e) {
            logger.warn("Could not capture error screenshot: {}", e.getMessage());
        }
    }

    /**
     * Cleanup resources.
     */
    private void cleanup() {
        if (FM != null) {
            try {
                FM.tearDown();
            } catch (Exception e) {
                logger.warn("Cleanup error: {}", e.getMessage());
            }
        }
    }

    /**
     * Creates required directories.
     */
    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(SCREENSHOTS_DIR));
            Files.createDirectories(Paths.get("target/traces"));
        } catch (IOException e) {
            logger.warn("Directory creation failed: {}", e.getMessage());
        }
    }

    /**
     * Generates test reports.
     */
    private static void generateReports() {
        // Cucumber HTML
        Path cucumberReport = Paths.get("target/cucumber-reports.html").toAbsolutePath();
        if (Files.exists(cucumberReport)) {
            System.out.println("📊 Cucumber Report: file://" + cucumberReport);
        }

        // Allure
        try {
            Process process = Runtime.getRuntime().exec(new String[]{
                    "allure", "generate", "allure-results", "-o", "allure-report", "--clean"
            });

            if (process.waitFor() == 0) {
                Path allureReport = Paths.get("allure-report/index.html").toAbsolutePath();
                System.out.println("📊 Allure Report: file://" + allureReport);
                System.out.println("💡 Or run: allure serve allure-results\n");
            }
        } catch (Exception e) {
            // Silently ignore if Allure not installed
        }
    }

    // ==================== UTILITIES ====================

    private static String formatTime(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) {
            return seconds + "s";
        }
        return (seconds / 60) + "m " + (seconds % 60) + "s";
    }

    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private static String timestamp() {
        return new SimpleDateFormat("HHmmss").format(new Date());
    }
}
