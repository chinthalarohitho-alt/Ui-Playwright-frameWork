@Gemini @Regression @GeminiGreeting
Feature: Send Greeting Prompt to Gemini

  Scenario: Send a greeting prompt and validate the response
    Given I navigate to the Gemini application
    When I enter the greeting prompt "How are you, Google?"
    Then I should wait for Gemini's response
    And the response should contain conversational keywords like "I am", "I'm", "doing well", or "Hello"
