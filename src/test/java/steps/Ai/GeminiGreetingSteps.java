package steps.Ai;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.Ai.GeminiChat;
import java.util.Arrays;
import java.util.List;

public class GeminiGreetingSteps {
    GeminiChat geminiChat = new GeminiChat();

    @When("I enter the greeting prompt {string}")
    public void i_enter_the_greeting_prompt(String prompt) {
        geminiChat.sendPrompt(prompt);
    }

    @Then("I should wait for Gemini's response")
    public void i_should_wait_for_gemini_response() {
        geminiChat.verifyResponseGenerated();
    }

    @And("the response should contain conversational keywords like {string}, {string}, {string}, or {string}")
    public void the_response_should_contain_keywords(String kw1, String kw2, String kw3, String kw4) {
        String responseText = geminiChat.getResponseText();
        Assert.assertFalse(responseText.isEmpty(), "Response should not be empty");
        
        List<String> keywords = Arrays.asList(kw1, kw2, kw3, kw4);
        boolean found = keywords.stream().anyMatch(kw -> responseText.toLowerCase().contains(kw.toLowerCase()));
        
        Assert.assertTrue(found, "Response did not contain any of the expected keywords: " + keywords + "\nActual response: " + responseText);
    }
}
