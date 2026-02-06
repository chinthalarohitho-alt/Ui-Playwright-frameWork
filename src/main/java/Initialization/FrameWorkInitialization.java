package Initialization;

import ch.qos.logback.classic.Logger;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.ViewportSize;
import config.Settings;
import config.frameWorkConfig;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class FrameWorkInitialization {

    private final frameWorkConfig config;
    private final Properties prop = new Properties();
    private final Properties envProperties = new Properties();
    private static final Logger logger = (Logger) LoggerFactory.getLogger(FrameWorkInitialization.class);

    // Default values
    private static final String DEFAULT_BROWSER = "chrome";
    private static final boolean DEFAULT_HEADLESS = false;
    private static final String DEFAULT_LOCALE = "en-US";
    private static final String DEFAULT_WINDOW_SIZE = "1280,800";
    private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds
    private static final int DEFAULT_NAVIGATION_TIMEOUT = 30000; // 30 seconds

    public FrameWorkInitialization() {
        this.config = frameWorkConfig.getInstance();
    }

    /**
     * Load properties from configuration files
     */
    public void loadProperties() throws IOException {
        try {
            // Load browser config
            loadPropertyFile("src/main/java/config/BrowserConfig.properties", prop);

            // Load environment-specific properties
            String env = System.getProperty("env").toLowerCase();
            String envFile = "src/main/resources/" + env + ".properties";
            loadPropertyFile(envFile, envProperties);

            logger.info("Loaded properties for environment: {}", env);
        } catch (IOException e) {
            logger.error("Failed to load properties", e);
            throw new IOException("Failed to load properties file: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to load property file
     */
    protected void loadPropertyFile(String filePath, Properties properties) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
            logger.debug("Loaded property file: {}", filePath);
        }
    }

    /**
     * Initialize browser with configuration from properties
     */
    public void initializeBrowser() {
        try {
            // Read browser configuration
            // Priority: Command line (-Dbrowser=chromium) > BrowserConfig.properties >
            // Default
            String browserOverride = System.getProperty("browser");
            String browserName = (browserOverride != null) ? browserOverride
                    : prop.getProperty("BrowserName", DEFAULT_BROWSER);

            // Priority: Command line (-Dheadless=true) > BrowserConfig.properties > Default
            String headlessOverride = System.getProperty("headless");
            boolean isHeadless = (headlessOverride != null) ? Boolean.parseBoolean(headlessOverride)
                    : Boolean.parseBoolean(prop.getProperty("Headless_status", String.valueOf(DEFAULT_HEADLESS)));
            String locale = prop.getProperty("Locale", DEFAULT_LOCALE);
            String windowSize = prop.getProperty("window_size", DEFAULT_WINDOW_SIZE);

            // Read timeout configurations
            int defaultTimeout = Integer.parseInt(prop.getProperty("default_timeout", String.valueOf(DEFAULT_TIMEOUT)));
            int navigationTimeout = Integer
                    .parseInt(prop.getProperty("navigation_timeout", String.valueOf(DEFAULT_NAVIGATION_TIMEOUT)));

            logger.info("Initializing browser - Name: {}, Headless: {}, Locale: {}, Window: {}",
                    browserName, isHeadless, locale, windowSize);

            // Configure launch options
            BrowserType.LaunchOptions launchOptions = createLaunchOptions(isHeadless, windowSize);

            // Get browser type and launch
            BrowserType browserType = getBrowserType(browserName, launchOptions);
            Browser browser = browserType.launch(launchOptions);
            config.setBrowser(browser);

            // Create browser context
            BrowserContext context = createBrowserContext(browser, locale, windowSize);
            config.setContext(context);

            // Start Playwright Tracing if enabled
            String enableTracing = prop.getProperty("enable_tracing", "false");
            if ("true".equalsIgnoreCase(enableTracing)) {
                context.tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true));
                logger.info("Playwright Tracing started");
            }

            // Create page
            Page page = context.newPage();
            config.setPage(page);

            // Set default timeouts
            page.setDefaultTimeout(defaultTimeout);
            page.setDefaultNavigationTimeout(navigationTimeout);

            logger.info("Browser initialized successfully: {} (headless: {})", browserName, isHeadless);
            logger.info("Timeouts set - Default: {}ms, Navigation: {}ms", defaultTimeout, navigationTimeout);

        } catch (Exception e) {
            logger.error("Failed to initialize browser", e);
            throw new RuntimeException("Browser initialization failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create launch options for browser
     */
    private BrowserType.LaunchOptions createLaunchOptions(boolean isHeadless, String windowSize) {
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless);

        // Add custom arguments
        List<String> args = new ArrayList<>();

        // Add window size argument
        if (windowSize != null && !windowSize.isEmpty()) {
            args.add("--window-size=" + windowSize);
        }

        // Add custom arguments from properties
        String argValue = prop.getProperty("argValue");
        if (argValue != null && !argValue.isEmpty()) {
            String[] customArgs = argValue.split(",");
            for (String arg : customArgs) {
                args.add(arg.trim());
            }
        }

        // Add common arguments for stability
        args.add("--disable-blink-features=AutomationControlled"); // Hide automation
        args.add("--no-sandbox"); // For Docker/CI environments
        args.add("--disable-dev-shm-usage"); // For Docker/CI environments

        if (!args.isEmpty()) {
            launchOptions.setArgs(args);
            logger.debug("Launch arguments: {}", args);
        }

        // Set slow motion if specified (for debugging)
        String slowMo = prop.getProperty("slow_motion");
        if (slowMo != null && !slowMo.isEmpty()) {
            try {
                double slowMotionValue = Double.parseDouble(slowMo);
                if (slowMotionValue > 0) {
                    launchOptions.setSlowMo(slowMotionValue);
                    logger.info("Slow motion enabled: {}ms per action", slowMotionValue);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid slow_motion value: {}", slowMo);
            }
        }

        return launchOptions;
    }

    /**
     * Create browser context with configuration
     */
    private BrowserContext createBrowserContext(Browser browser, String locale, String windowSize) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setLocale(locale)
                .setViewportSize(parseViewportSize(windowSize));

        // Set user agent if specified
        String userAgent = prop.getProperty("user_agent");
        if (userAgent != null && !userAgent.isEmpty()) {
            contextOptions.setUserAgent(userAgent);
        }

        // Set geolocation if specified
        String latitude = prop.getProperty("geolocation_latitude");
        String longitude = prop.getProperty("geolocation_longitude");
        if (latitude != null && longitude != null) {
            contextOptions.setGeolocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
            contextOptions.setPermissions(List.of("geolocation"));
        }

        // Set timezone if specified
        String timezone = prop.getProperty("timezone");
        if (timezone != null && !timezone.isEmpty()) {
            contextOptions.setTimezoneId(timezone);
        }

        // Accept downloads
        contextOptions.setAcceptDownloads(true);

        // Set video recording if specified
        String recordVideo = prop.getProperty("record_video");
        if ("true".equalsIgnoreCase(recordVideo)) {
            contextOptions.setRecordVideoDir(java.nio.file.Paths.get("videos/"));
            logger.info("Video recording enabled. Videos will be saved to: videos/");
        }

        logger.debug("Browser context options: Locale={}, Viewport={}", locale, windowSize);

        return browser.newContext(contextOptions);
    }

    /**
     * Get browser type based on browser name
     */
    private BrowserType getBrowserType(String browserName, BrowserType.LaunchOptions options) {
        Playwright playwright = config.getPlaywright();

        return switch (browserName.toLowerCase()) {
            case "chrome" -> {
                options.setChannel("chrome");
                yield playwright.chromium();
            }
            case "chromium" -> playwright.chromium();
            case "firefox" -> playwright.firefox();
            case "edge" -> {
                options.setChannel("msedge");
                yield playwright.chromium();
            }
            case "webkit", "safari" -> playwright.webkit();
            default -> {
                logger.warn("Unknown browser '{}', defaulting to Chrome", browserName);
                options.setChannel("chrome");
                yield playwright.chromium();
            }
        };
    }

    /**
     * Parse viewport size from string and return ViewportSize object
     */
    private ViewportSize parseViewportSize(String windowSize) {
        try {
            if (windowSize == null || windowSize.isEmpty()) {
                return new ViewportSize(1280, 800);
            }

            String[] parts = windowSize.split("[,x]");
            if (parts.length != 2) {
                logger.warn("Invalid window size format '{}', using default 1920x1080", windowSize);
                return new ViewportSize(1280, 800);
            }

            int width = Integer.parseInt(parts[0].trim());
            int height = Integer.parseInt(parts[1].trim());

            // Validate dimensions
            if (width < 100 || height < 100) {
                logger.warn("Window size too small {}, using default 1920x1080", windowSize);
                return new ViewportSize(1280, 800);
            }

            return new ViewportSize(width, height);

        } catch (NumberFormatException e) {
            logger.warn("Invalid window size '{}', using default 1920x1080", windowSize);
            return new ViewportSize(1280, 800);
        }
    }

    /**
     * Setup - Load properties, initialize browser, and navigate to URL
     */
    public void setUp() throws IOException {
        try {
            logger.info("Starting framework setup...");

            loadProperties();
            initializeBrowser();

            String url = Settings.Url;
            if (url == null || url.isEmpty()) {
                throw new IllegalStateException("URL not found in environment properties");
            }

            logger.info("Navigating to: {}", url);
            config.getPage().navigate(url);
            config.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);

            logger.info("Framework setup completed successfully");

        } catch (Exception e) {
            logger.error("Setup failed", e);
            tearDown(); // Cleanup on failure
            throw e;
        }
    }

    /**
     * Teardown - Cleanup resources after each scenario
     * ✅ FIXED: Now calls cleanupScenario() instead of cleanup()
     * This closes Browser/Context/Page but keeps Playwright alive
     */
    public void tearDown() {
        try {
            logger.info("Starting scenario cleanup...");
            config.cleanupScenario(); // ✅ Use cleanupScenario() - keeps Playwright alive
            logger.info("Scenario cleanup completed successfully");
        } catch (Exception e) {
            logger.error("Error during scenario cleanup", e);
        }
    }

    /**
     * Complete shutdown - Call only at the end of ALL tests (in @AfterAll)
     * Closes Playwright instance completely
     */
    public void shutdownAll() {
        try {
            logger.info("Starting complete framework shutdown...");
            config.cleanupAll(); // ✅ Closes everything including Playwright
            logger.info("Framework shutdown completed successfully");
        } catch (Exception e) {
            logger.error("Error during complete shutdown", e);
        }
    }

    // ==================== GETTERS ====================

    public Page getPage() {
        return config.getPage();
    }

    public Browser getBrowser() {
        return config.getBrowser();
    }

    public BrowserContext getContext() {
        return config.getContext();
    }

    public Properties getProperties() {
        return prop;
    }

    public Properties getEnvProperties() {
        return envProperties;
    }

    public String getEnvironment() {
        return System.getProperty("env", "alpha");
    }
}
