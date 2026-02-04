#=================================================================
#                Form Page
#==================================================================
@smoke
Feature: Smoke Test
# -----------------------------------------------------------------
#           Back Ground
# -----------------------------------------------------------------

  Background: Logging and navigating to the Form Tab
    Given I navigate to the QA Playground homepage

  # -----------------------------------------------------------------
  # TEXT INPUT
  # -----------------------------------------------------------------
@SmokeTestOnForm
Scenario: Validate successful end-to-end form submission
  Given I click on the "Forms" Module
#  When I fill all fields with valid data
#  And I submit the form
#  Then I should see a successful submission message