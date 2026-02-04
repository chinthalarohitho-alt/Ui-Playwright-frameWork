package utilze;

import Initialization.FrameWorkInitialization;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright utility class for browser automation.
 * Provides reusable methods for clicking, filling, assertions, and navigation.
 * All methods throw exceptions to properly fail tests on errors.
 */
public class playwright extends FrameWorkInitialization {
    private static final Logger logger = LoggerFactory.getLogger(playwright.class);
    private static final Random random = new Random();
    
    // Global variable storage shared across all instances
    private static final Map<String, Object> variables = new HashMap<>();

    // ==================== VARIABLE STORAGE METHODS ====================

    /**
     * Stores a value in the global variable map.
     * @param key The key to identify the variable
     * @param value The value to store
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
        logger.debug("Stored variable: {} = {}", key, value);
    }

    /**
     * Retrieves a value from the global variable map.
     * @param key The key of the variable
     * @return The value as Object, or null if not found
     */
    public Object getVariable(String key) {
        Object value = variables.get(key);
        logger.debug("Retrieved variable: {} = {}", key, value);
        return value;
    }

    /**
     * Retrieves a value as a String.
     * @param key The key of the variable
     * @return The value as String, or empty string if null
     */
    public String getVariableAsString(String key) {
        Object value = variables.get(key);
        return value != null ? value.toString() : "";
    }

    // ==================== CLICK ACTIONS ====================

    /**
     * Internal helper to perform clicks with standardized logging and error handling.
     */
    private void internalClick(Locator locator, String description) {
        try {
            logger.debug("Clicking: {}", description);
            locator.click();
            logger.debug("Clicked successfully: {}", description);
        } catch (TimeoutError e) {
            String error = String.format("Element not found or not clickable within timeout: %s", description);
            logger.error(error, e);
            throw new AssertionError(error, e);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to click element: %s. Reason: %s", description, e.getMessage());
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Clicks on an element using a CSS or XPath selector.
     */
    public void click(String selector) {
        internalClick(getPage().locator(selector), selector);
    }

    /**
     * Clicks on an element by its ARIA role and name.
     */
    public void clickByRole(AriaRole role, String name) {
        internalClick(getPage().getByRole(role, new Page.GetByRoleOptions().setName(name)),
                String.format("Role: %s, Name: %s", role, name));
    }

    public void clickButton(String name) {
        clickByRole(AriaRole.BUTTON, name);
    }

    public void clickLink(String name) {
        clickByRole(AriaRole.LINK, name);
    }

    public void clickCheckbox(String name) {
        clickByRole(AriaRole.CHECKBOX, name);
    }

    public void clickRadio(String name) {
        clickByRole(AriaRole.RADIO, name);
    }

    public void clickTab(String name) {
        clickByRole(AriaRole.TAB, name);
    }

    public void clickMenuItem(String name) {
        clickByRole(AriaRole.MENUITEM, name);
    }

    /**
     * Clicks on an element by its associated label text.
     */
    public void clickByLabel(String label) {
        internalClick(getPage().getByLabel(label), "Label: " + label);
    }

    /**
     * Clicks on an element by its placeholder text.
     */
    public void clickByPlaceholder(String placeholder) {
        internalClick(getPage().getByPlaceholder(placeholder), "Placeholder: " + placeholder);
    }

    /**
     * Clicks on an element by its alt-text (typically images).
     */
    public void clickByAltText(String altText) {
        internalClick(getPage().getByAltText(altText), "AltText: " + altText);
    }

    /**
     * Clicks on an element by its title attribute.
     */
    public void clickByTitle(String title) {
        internalClick(getPage().getByTitle(title), "Title: " + title);
    }

    /**
     * Clicks on an element using its test-id attribute.
     */
    public void clickByTestId(String testId) {
        internalClick(getPage().getByTestId(testId), "TestId: " + testId);
    }

    /**
     * Clicks element multiple times with delay between clicks.
     * Use for double-click (clickCount=2) or triple-click (clickCount=3).
     */
    public void clickWithOptions(String locator, int clickCount, int delay) {
        try {
            logger.debug("Clicking element {} with count={}, delay={}ms", locator, clickCount, delay);
            getPage().locator(locator).click(new Locator.ClickOptions()
                    .setClickCount(clickCount)
                    .setDelay(delay));
        } catch (PlaywrightException e) {
            String error = String.format("Failed to click %s with options", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Double-clicks on an element.
     * Useful for opening files or folders.
     */
    public void doubleClick(String locator) {
        try {
            logger.debug("Double clicking: {}", locator);
            getPage().locator(locator).dblclick();
        } catch (PlaywrightException e) {
            String error = String.format("Failed to double click: %s", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Right-clicks on element to open context menu.
     */
    public void rightClick(String locator) {
        try {
            logger.debug("Right clicking: {}", locator);
            getPage().locator(locator).click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));
        } catch (PlaywrightException e) {
            String error = String.format("Failed to right click: %s", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    // ==================== INPUT/TEXT METHODS ====================

    /**
     * Fills input field with text. Clears existing value first.
     * Fastest way to enter text. Use for input fields, textareas.
     */
    public void fill(String locator, String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null for locator: " + locator);
        }

        try {
            logger.debug("Filling '{}' with text: '{}'", locator, text);
            getPage().locator(locator).fill(text);
            logger.debug("Filled successfully: {}", locator);
        } catch (TimeoutError e) {
            String error = String.format("Element not found or not editable within timeout: %s", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to fill '%s' with text '%s'. Reason: %s",
                    locator, text, e.getMessage());
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Clears input field then fills with new text.
     * Use when fill() alone doesn't clear properly.
     */
    public void clearAndFill(String locator, String text) {
        try {
            logger.debug("Clearing and filling '{}' with: '{}'", locator, text);
            getPage().locator(locator).clear();
            getPage().locator(locator).fill(text);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to clear and fill %s", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Types text character by character with delay.
     * Use for live search or triggering keypress events. Slower than fill().
     */
    public void type(String locator, String text, int delayMs) {
        try {
            logger.debug("Typing '{}' into {} with delay {}ms", text, locator, delayMs);
            getPage().locator(locator).pressSequentially(text,
                    new Locator.PressSequentiallyOptions().setDelay(delayMs));
        } catch (PlaywrightException e) {
            String error = String.format("Failed to type into %s", locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Presses keyboard key on element (Enter, Tab, Escape, etc).
     * Use for submitting forms, navigating fields, keyboard shortcuts.
     */
    public void pressKey(String locator, String key) {
        try {
            logger.debug("Pressing key '{}' on element: {}", key, locator);
            getPage().locator(locator).press(key);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to press key '%s' on %s", key, locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    // ==================== ASSERTIONS ====================

    /**
     * Asserts element is visible on page.
     * Waits up to 5 seconds. Fails test if not visible.
     */
    public void assertVisible(String locator) {
        try {
            logger.debug("Asserting visibility of: {}", locator);
            assertThat(getPage().locator(locator)).isVisible();
            logger.debug("Element is visible: {}", locator);
        } catch (AssertionError e) {
            String error = String.format("Element is NOT visible: %s", locator);
            logger.error(error);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Asserts element is visible on page.
     * Waits up to 5 seconds. Fails test if not visible.
     */
    public void assertIsNotVisible(String locator) {
        try {
            logger.debug("Asserting for not visibility of: {}", locator);
            assertThat(getPage().locator(locator)).not().isVisible();
            logger.debug("Element is not visible: {}", locator);
        } catch (AssertionError e) {
            String error = String.format("Element is visible: %s", locator);
            logger.error(error);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Asserts element has exact text (case-sensitive).
     * Shows expected vs actual text if fails.
     */
    public void assertHasText(String locator, String expectedText) {
        try {
            logger.debug("Asserting text '{}' for element: {}", expectedText, locator);
            assertThat(getPage().locator(locator)).hasText(expectedText);
            logger.debug("Text assertion passed for: {}", locator);
        } catch (AssertionError e) {
            String actualText = getText(locator);
            String error = String.format(
                    "Text mismatch for %s.%nExpected: '%s'%nActual: '%s'",
                    locator, expectedText, actualText
            );
            logger.error(error);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Asserts element contains text (partial match).
     * Element can have additional text before/after.
     */
    public void assertContainsText(String locator, String text) {
        try {
            logger.debug("Asserting '{}' contains text: '{}'", locator, text);
            assertThat(getPage().locator(locator)).containsText(text);
        } catch (AssertionError e) {
            String actualText = getText(locator);
            String error = String.format(
                    "Text '%s' not found in %s.%nActual text: '%s'",
                    text, locator, actualText
            );
            logger.error(error);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Asserts current page URL matches expected URL.
     * Shows expected vs actual URL if fails.
     */
    public void assertPageHasURL(String url) {
        try {
            logger.debug("Asserting page URL: {}", url);
            assertThat(getPage()).hasURL(url);
        } catch (AssertionError e) {
            String actualUrl = getCurrentUrl();
            String error = String.format(
                    "URL mismatch.%nExpected: %s%nActual: %s",
                    url, actualUrl
            );
            logger.error(error);
            throw new AssertionError(error, e);
        }
    }

    // ==================== SAFE METHODS (NON-CRITICAL) ====================

    /**
     * Checks if element is visible. Returns false instead of throwing.
     * Use for optional elements that may or may not exist.
     */
    public boolean isVisibleSafe(String locator) {
        try {
            return getPage().locator(locator).isVisible();
        } catch (Exception e) {
            logger.warn("Error checking visibility for {}: {}", locator, e.getMessage());
            return false;
        }
    }

    /**
     * Gets text from element. Returns empty string if fails.
     * Use when text is optional and shouldn't fail test.
     */
    public String getTextSafe(String locator) {
        try {
            return getPage().locator(locator).textContent();
        } catch (Exception e) {
            logger.warn("Error getting text from {}: {}", locator, e.getMessage());
            return "";
        }
    }

    /**
     * Gets attribute value. Returns null if fails.
     * Use for optional attributes that may not exist.
     */
    public String getAttributeSafe(String locator, String attributeName) {
        try {
            return getPage().locator(locator).getAttribute(attributeName);
        } catch (Exception e) {
            logger.warn("Error getting attribute '{}' from {}: {}",
                    attributeName, locator, e.getMessage());
            return null;
        }
    }

    // ==================== WAIT METHODS ====================

    /**
     * Waits for element to become visible with custom timeout.
     * Polls every 500ms. Fails test if timeout exceeded.
     */
    public void waitForElementVisibility(String locator, int timeoutMs) {
        try {
            logger.debug("Waiting for element visibility: {} (timeout: {}ms)", locator, timeoutMs);
            getPage().locator(locator).waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(timeoutMs));
            logger.debug("Element became visible: {}", locator);
        } catch (TimeoutError e) {
            String error = String.format(
                    "Element did NOT become visible within %dms: %s",
                    timeoutMs, locator
            );
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Waits for element visibility with 30 second default timeout.
     */
    public void waitForElementVisibility(String locator) {
        waitForElementVisibility(locator, 30000);
    }

    /**
     * Waits for element to become invisible/detached with custom timeout.
     * Polls every 500ms. Fails test if timeout exceeded.
     */
    public void waitForElementInvisibility(String locator, int timeoutMs) {
        try {
            logger.debug("Waiting for element invisibility: {} (timeout: {}ms)", locator, timeoutMs);
            getPage().locator(locator).waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.HIDDEN)
                    .setTimeout(timeoutMs));
            logger.debug("Element became invisible: {}", locator);
        } catch (TimeoutError e) {
            String error = String.format(
                    "Element did NOT become invisible within %dms: %s",
                    timeoutMs, locator
            );
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    /**
     * Waits for element invisibility with 30 second default timeout.
     */
    public void waitForElementInvisibility(String locator) {
        waitForElementInvisibility(locator, 30000);
    }

    /**
     * Waits for page to finish loading.
     * Use after navigation or form submission.
     */
    public void waitForLoadState() {
        logger.debug("Waiting for page load state");
        getPage().waitForLoadState(LoadState.LOAD);
    }

    /**
     * Waits for all network activity to stop (no requests for 500ms).
     * Most reliable wait. Use after API calls or dynamic content loading.
     */
    public void waitForNetworkIdle() {
        logger.debug("Waiting for network idle");
        getPage().waitForLoadState(LoadState.NETWORKIDLE);
    }

    // ==================== SCREENSHOT METHODS ====================

    /**
     * Takes screenshot and saves to file. Throws if fails.
     * File path must include extension (.png, .jpg).
     */
    public void takeScreenshot(String filePath) {
        try {
            logger.debug("Taking screenshot: {}", filePath);
            getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
            logger.info("Screenshot saved: {}", filePath);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to save screenshot to: %s", filePath);
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Takes screenshot and saves to file. Logs warning if fails.
     * Safe for teardown - doesn't fail test if screenshot fails.
     */
    public void takeScreenshotSafe(String filePath) {
        try {
            getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filePath)));
            logger.info("Screenshot saved: {}", filePath);
        } catch (Exception e) {
            logger.warn("Failed to save screenshot to {}: {}", filePath, e.getMessage());
        }
    }

    // ==================== FILE UPLOAD ====================

    /**
     * Uploads file to input element. Validates file exists first.
     * Throws error if file not found or upload fails.
     */
    public void uploadFile(String locator, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        Path path = Paths.get(filePath);
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        try {
            logger.debug("Uploading file '{}' to element: {}", filePath, locator);
            getPage().locator(locator).setInputFiles(path);
            logger.info("File uploaded successfully: {}", filePath);
        } catch (PlaywrightException e) {
            String error = String.format("Failed to upload file '%s' to %s", filePath, locator);
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    // ==================== NAVIGATION ====================

    /**
     * Navigates to URL. Single attempt, throws if fails.
     * Use navigateTo(url, retries) for retry logic.
     */
    public void navigateTo(String url) {
        navigateTo(url, 1);
    }

    /**
     * Navigates to URL with retry logic. Retries on failure.
     * Waits 1 second between retries. Throws after max retries.
     */
    public void navigateTo(String url, int maxRetries) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                logger.info("Navigating to: {} (attempt {}/{})", url, attempt, maxRetries);
                getPage().navigate(url);
                logger.info("Navigation successful: {}", url);
                return;
            } catch (PlaywrightException e) {
                lastException = e;
                logger.warn("Navigation attempt {} failed for {}: {}",
                        attempt, url, e.getMessage());

                if (attempt < maxRetries) {
                    logger.info("Retrying navigation...");
                    waitForTimeout(1000);
                }
            }
        }

        String error = String.format(
                "Failed to navigate to %s after %d attempts",
                url, maxRetries
        );
        logger.error(error, lastException);
        throw new AssertionError(error, lastException);
    }

    // ==================== SELECT/DROPDOWN ====================

    /**
     * Selects dropdown option by value attribute.
     * Example: <option value="usa">United States</option> → selectByValue("#country", "usa")
     */
    public void selectByValue(String locator, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null for: " + locator);
        }

        try {
            logger.debug("Selecting option '{}' in dropdown: {}", value, locator);
            getPage().locator(locator).selectOption(value);
            logger.debug("Option selected successfully");
        } catch (PlaywrightException e) {
            String error = String.format(
                    "Failed to select option '%s' in dropdown %s",
                    value, locator
            );
            logger.error(error, e);
            throw new AssertionError(error, e);
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Generates random string with length between min and max.
     * Example: generateUniqueString(5, 10) → returns 5-10 character string
     */
    public static String generateUniqueString(int minLength, int maxLength) {
        if (minLength < 1 || maxLength < minLength) {
            throw new IllegalArgumentException(
                    String.format("Invalid length range: min=%d, max=%d", minLength, maxLength)
            );
        }
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, Math.min(length, uuid.length()));
    }

    /**
     * Generates random email address.
     * Example: test_a1b2c3d4@test.com
     */
    public static String generateRandomEmail() {
        return "test_" + generateUniqueString(8, 12) + "@test.com";
    }

    /**
     * Generates random number with specified digits.
     * Example: generateRandomNumber(5) → "47382"
     */
    public static String generateRandomNumber(int digits) {
        if (digits < 1 || digits > 18) {
            throw new IllegalArgumentException("Digits must be between 1 and 18");
        }
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            number.append(random.nextInt(10));
        }
        return number.toString();
    }

    /**
     * Gets text content from element. Throws if fails.
     * Use getTextSafe() if text is optional.
     */
    public String getText(String locator) {
        return getPage().locator(locator).textContent();
    }

    /**
     * Gets current page URL.
     */
    public String getCurrentUrl() {
        return getPage().url();
    }

    /**
     * Waits for specified milliseconds. Use sparingly - prefer smart waits.
     */
    public void waitForTimeout(int milliseconds) {
        getPage().waitForTimeout(milliseconds);
    }

    // ==================== FILE HANDLING ====================

    /**
     * Reads text file content and returns as String.
     * @param filePath The absolute path to the text file
     * @return The content of the file as String
     */
    public String readTextFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        try {
            Path path = Paths.get(filePath);
            if (!path.toFile().exists()) {
                throw new IllegalArgumentException("File does not exist: " + filePath);
            }
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            String error = String.format("Failed to read file: %s", filePath);
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Retrieves a file path string from src/main/java/config/FilePaths.txt based on a keyword.
     * Format in file: keyword = "path/to/file"
     * @param keyword The key to look for
     * @return The file path associated with the keyword
     */
    public String getFilePath(String keyword) {
        String configPath = "src/main/java/config/FilePaths.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(configPath));
            for (String line : lines) {
                // Determine split logic - simply looking for the keyword at the start
                // Handling format: key = "value"
                if (line.trim().startsWith(keyword)) {
                    String[] parts = line.split("=", 2);
                    if (parts.length >= 2) {
                        // Get value part, trim whitespace and quotes
                        return parts[1].trim().replace("\"", "").replace("\\", "/");
                    }
                }
            }
        } catch (IOException e) {
            String error = String.format("Failed to read config file: %s", configPath);
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        throw new IllegalArgumentException("Keyword not found in FilePaths.txt: " + keyword);
    }

    
}
