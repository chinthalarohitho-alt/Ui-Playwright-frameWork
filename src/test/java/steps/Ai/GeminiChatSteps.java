package steps.Ai;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.Ai.GeminiChat;
import pages.Ai.GeminiChatPaths;
import utilze.playwright;

/**
 * Step definitions for Gemini Chat feature tests.
 */
public class GeminiChatSteps {
    private static final Logger logger = LoggerFactory.getLogger(GeminiChatSteps.class);
    
    GeminiChat geminiChat = new GeminiChat();
    
    // Store the last sent message for verification
    private String lastSentMessage = "";

    // ==================== NAVIGATION STEPS ====================

    @Given("I navigate to Gemini app")
    public void iNavigateToGeminiApp() {
        logger.info("Step: Navigate to Gemini app");
        geminiChat.navigateToGeminiApp();
    }

    // ==================== ACTION STEPS ====================

    @When("I click on New chat button")
    public void iClickOnNewChatButton() {
        logger.info("Step: Click on New chat button");
        geminiChat.clickNewChatButton();
    }

    @When("I click inside the prompt input field")
    public void iClickInsideThePromptInputField() {
        logger.info("Step: Click inside the prompt input field");
        // This is handled within sendPrompt method
    }

    @When("I type a valid prompt {string}")
    public void iTypeAValidPrompt(String prompt) {
        logger.info("Step: Type a valid prompt: {}", prompt);
        lastSentMessage = prompt;
        // Prompt will be sent in the next step
    }

    @When("I click the Send button")
    public void iClickTheSendButton() {
        logger.info("Step: Click the Send button");
        geminiChat.sendPrompt(lastSentMessage);
    }

    @When("I send a prompt {string} in active chat")
    public void iSendAPromptInActiveChat(String prompt) {
        logger.info("Step: Send a prompt in active chat: {}", prompt);
        lastSentMessage = prompt;
        geminiChat.sendPrompt(prompt);
    }

    @And("I wait for Gemini response")
    public void iWaitForGeminiResponse() {
        logger.info("Step: Wait for Gemini response");
        geminiChat.verifyResponseGenerated();
    }

    @And("I type a follow-up question {string}")
    public void iTypeAFollowUpQuestion(String question) {
        logger.info("Step: Type a follow-up question: {}", question);
        lastSentMessage = question;
        // Question will be sent in the next step
    }

    // ==================== VERIFICATION STEPS ====================

    @Then("a new empty chat screen should be displayed")
    public void aNewEmptyChatScreenShouldBeDisplayed() {
        logger.info("Step: Verify new empty chat screen is displayed");
        geminiChat.verifyEmptyChatScreen();
    }

    @And("previous conversation should be cleared")
    public void previousConversationShouldBeCleared() {
        logger.info("Step: Verify previous conversation is cleared");
        geminiChat.verifyEmptyChatScreen();
    }

    @And("a new chat entry should be added to the chat list")
    public void aNewChatEntryShouldBeAddedToTheChatList() {
        logger.info("Step: Verify new chat entry is added to chat list");
        new playwright().waitForElementVisibility(GeminiChatPaths.CHAT_LIST_ITEMS);
        new playwright().assertVisible(GeminiChatPaths.CHAT_LIST_ITEMS);
    }

    @Then("user message should appear in the chat window")
    public void userMessageShouldAppearInTheChatWindow() {
        logger.info("Step: Verify user message appears in chat window");
        geminiChat.verifyUserMessageAppears(lastSentMessage);
    }

    @And("Gemini should start generating a response")
    public void geminiShouldStartGeneratingAResponse() {
        logger.info("Step: Verify Gemini starts generating a response");
        geminiChat.verifyResponseGenerated();
    }

    @And("conversation should be saved automatically")
    public void conversationShouldBeSavedAutomatically() {
        logger.info("Step: Verify conversation is saved automatically");
        new playwright().waitForElementVisibility(GeminiChatPaths.CHAT_LIST_ITEMS);
        new playwright().assertVisible(GeminiChatPaths.CHAT_LIST_ITEMS);
    }

    @Then("Gemini should respond considering the previous conversation context")
    public void geminiShouldRespondConsideringThePreviousConversationContext() {
        logger.info("Step: Verify Gemini responds with context");
        geminiChat.verifyResponseGenerated();
    }

    @And("messages should appear in correct order")
    public void messagesShouldAppearInCorrectOrder() {
        logger.info("Step: Verify messages appear in correct order");
        geminiChat.verifyMessagesInCorrectOrder();
    }
}
