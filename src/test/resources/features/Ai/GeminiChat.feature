@GeminiChat @ui @regression
Feature: Gemini Chat Functionality Validation

  Background: Navigate to Gemini App
    Given I navigate to Gemini app

  @FC-01 @NewChat
  Scenario: FC-01 - Create a new chat
    When I click on New chat button
    Then a new empty chat screen should be displayed
    And previous conversation should be cleared
    And a new chat entry should be added to the chat list

  @FC-02 @FirstMessage
  Scenario: FC-02 - Send first message in a new chat
    When I click on New chat button
    And I click inside the prompt input field
    And I type a valid prompt "What is Java?"
    And I click the Send button
    Then user message should appear in the chat window
    And Gemini should start generating a response
    And conversation should be saved automatically

  @FC-03 @ContextualConversation
  Scenario: FC-03 - Continue conversation using context
    When I click on New chat button
    And I send a prompt "What is Java?" in active chat
    And I wait for Gemini response
    And I type a follow-up question "What are its main features?"
    And I click the Send button
    Then Gemini should respond considering the previous conversation context
    And messages should appear in correct order
