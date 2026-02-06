@Gemini @Regression @GeminiNewChat
Feature: Start a New Chat in Gemini

  Scenario: Start a new empty chat session
    Given I navigate to the Gemini application
    When I click on the "New Chat" button
    Then I should see an empty chat screen
    And the prompt input field should be focused
