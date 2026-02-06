package steps.Ai;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.Ai.GeminiChat;

public class GeminiNewChatSteps {
    GeminiChat geminiChat = new GeminiChat();

    @Given("I navigate to the Gemini application")
    public void i_navigate_to_gemini_app() {
        geminiChat.navigateToGeminiApp();
    }

    @When("I click on the {string} button")
    public void i_click_on_the_button(String buttonName) {
        geminiChat.clickNewChatButton();
    }

    @Then("I should see an empty chat screen")
    public void i_should_see_empty_chat() {
        geminiChat.verifyEmptyChatScreen();
    }

    @Then("the prompt input field should be focused")
    public void prompt_input_should_be_focused() {
        Assert.assertTrue(geminiChat.verifyPromptInputFocused(), "Prompt input field should be focused");
    }
}
