@Agents @ui @regression
Feature: Forms Page End-to-End Validation
# -----------------------------------------------------------------
# Background
# -----------------------------------------------------------------
  Background: Logging and navigating to the Form Tab
    Given I navigate to the chatGpt
    And I configured the chatGpt for Fetching the params: "Accuracy, Hallucination, Relevance, Clarity, Completeness" based on the query

# -----------------------------------------------------------------
#          Agents Test cases
# -----------------------------------------------------------------

  @Agents
  Scenario Outline: User adds a laptop to the cart and removes it successfully
    Given user open the gemini in the new tab
    # When user send input "<input>" to the gemini
    # Then user capture the response
    # When user pass that response to the configured chatgpt
    # Then chatgpt pass the params in response
    # And all params should be greater than 2.5
    # Examples:
    #   | input                   |
    #   | who is ceo of google    |
    #   | who is president of usa |
    #   | where is india          |
    #   | what is movie           |


