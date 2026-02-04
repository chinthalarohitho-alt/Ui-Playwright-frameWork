package steps.Ai;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.Ai.Ai;

public class AISteps {
    
    Ai ai = new Ai();

    @Given("I navigate to the chatGpt")
    public void iNavigateToTheChatGpt() {
        ai.LandOnChatgpt();
    }

    @And("I configured the chatGpt for Fetching the params: {string} based on the query")
    public void iConfiguredTheChatGptForFetchingTheParamsBasedOnTheQuery(String params) {
        ai.configureChatGpt(params);
    }

    @Given("user open the gemini in the new tab")
    public void theUserOpenTheGeminiInTheNewTab() {
       ai.openGeminiInNewTab();
    }

    @When("user send input {string} to the gemini")
    public void userSendInputToTheGemini(String input) {
        ai.inputToGemini(input);
    }

    @Then("user capture the response")
    public void userCaptureTheResponse() {
       ai.userCaptureResponse();
    }

    @When("user pass that response to the configured chatgpt")
    public void userPassThatResponseToTheConfiguredChatgpt() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("chatgpt pass the params in response")
    public void chatgptPassTheParamsInResponse() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @And("all params should be greater than {double}")
    public void allParamsShouldBeGreaterThan(int arg0, int arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
