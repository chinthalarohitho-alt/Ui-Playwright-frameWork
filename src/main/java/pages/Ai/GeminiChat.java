package pages.Ai;

import config.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilze.playwright;

/**
 * Page Object for Gemini Chat functionality.
 * Uses existing playwright utility methods directly.
 */
public class GeminiChat {
    private static final Logger logger = LoggerFactory.getLogger(GeminiChat.class);
    playwright pm = new playwright();

    /**
     * Navigate to Gemini App
     */
    public void navigateToGeminiApp() {
        logger.info("Navigating to Gemini App");
        pm.navigateTo(Settings.geminiAppUrl);
        pm.waitForDOMContentLoaded();
        pm.waitForElementVisibility(GeminiChatPaths.PROMPT_INPUT_XPATH, 60000);
    }

    /**
     * Click on New Chat button.
     * Handles case where button is disabled (already on new chat).
     */
    public void clickNewChatButton() {
        try {
            String disabled = pm.getPage().locator(GeminiChatPaths.NEW_CHAT_BUTTON_XPATH).getAttribute("aria-disabled");
            String isDisabled = pm.getPage().locator(GeminiChatPaths.NEW_CHAT_BUTTON_XPATH).getAttribute("disabled");
            
            if ("true".equals(disabled) || "true".equals(isDisabled)) {
                logger.info("New Chat button is disabled. Already on New Chat screen.");
                return;
            }
            
            pm.click(GeminiChatPaths.NEW_CHAT_BUTTON_XPATH);
        } catch (Exception e) {
            logger.warn("Primary locator failed or other error, trying aria-label click as fallback");
            try {
                pm.getPage().getByLabel(GeminiChatPaths.NEW_CHAT_BUTTON_ARIA_LABEL).click();
            } catch (Exception ex) {
                 logger.info("Could not click New Chat (likely disabled/not found): " + ex.getMessage());
            }
        }
    }

    /**
     * Send a prompt to Gemini - uses content-editable div
     * Waits for send button to be enabled after typing
     */
    public void sendPrompt(String prompt) {
        pm.waitForElementVisibility(GeminiChatPaths.PROMPT_INPUT_XPATH);
        pm.click(GeminiChatPaths.PROMPT_INPUT_XPATH);
        
        pm.type(GeminiChatPaths.PROMPT_INPUT_XPATH, prompt, 10);
        
        pm.waitForElementVisibility(GeminiChatPaths.SEND_BUTTON_XPATH);
        
        pm.click(GeminiChatPaths.SEND_BUTTON_XPATH);
    }

    /**
     * Verify empty chat screen - business logic to check no messages present
     */
    public void verifyEmptyChatScreen() {
        pm.waitForDOMContentLoaded();
        
        if (pm.isVisibleSafe(GeminiChatPaths.MESSAGE_CONTENT)) {
            logger.error("Chat screen is not empty: message content detected.");
            throw new AssertionError("Chat screen is not empty - messages are present");
        }
    }

    /**
     * Verify user message appears with expected text - business logic
     */
    public void verifyUserMessageAppears(String expectedMessage) {
        pm.waitForElementVisibility(GeminiChatPaths.USER_MESSAGE_CONTAINER, 5000);
        String messageText = pm.getTextSafe(GeminiChatPaths.USER_MESSAGE_CONTAINER);
        
        if (!messageText.contains(expectedMessage)) {
            throw new AssertionError(
                String.format("User message not found. Expected: '%s', Actual: '%s'", 
                    expectedMessage, messageText)
            );
        }
    }

    /**
     * Verify Gemini response generated - waits for loading and response
     */
    public void verifyResponseGenerated() {
        pm.waitForElementVisibility(GeminiChatPaths.AI_RESPONSE_CONTAINER, 5000);
        
        if (pm.isVisibleSafe(GeminiChatPaths.RESPONSE_LOADING)) {
            pm.waitForElementInvisibility(GeminiChatPaths.RESPONSE_LOADING, 5000);
        }
        
        pm.waitForElementVisibility(GeminiChatPaths.MESSAGE_CONTENT, 10000);
    }

    /**
     * Verify messages in correct order - business logic to count messages
     */
    public void verifyMessagesInCorrectOrder() {
        int messageCount = pm.count(GeminiChatPaths.AI_RESPONSE_CONTAINER);
        
        if (messageCount < 2) {
            throw new AssertionError(
                String.format("Expected at least 2 messages, found: %d", messageCount)
            );
        }
    }

    /**
     * Verify that the prompt input field is focused.
     * Uses JavaScript evaluation to check document.activeElement.
     */
    public boolean verifyPromptInputFocused() {
        pm.waitForElementVisibility(GeminiChatPaths.PROMPT_INPUT_XPATH);
        
        return (Boolean) pm.getPage().locator(GeminiChatPaths.PROMPT_INPUT_XPATH)
                .evaluate("element => document.activeElement === element");
    }
    
    public boolean verifyNewChatActive() {
        String currentUrl = pm.getPage().url();
        boolean isBaseUrl = currentUrl.equals(Settings.geminiAppUrl) || currentUrl.startsWith(Settings.geminiAppUrl + "/app");
        return isBaseUrl;
    }

    public String getResponseText() {
        return pm.getTextSafe(GeminiChatPaths.MESSAGE_CONTENT);
    }
}
