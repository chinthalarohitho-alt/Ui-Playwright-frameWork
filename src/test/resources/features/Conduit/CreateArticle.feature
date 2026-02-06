@Conduit @Regression
Feature: Create and Validate Article
  As a user of Conduit
  I want to create, view, and delete articles
  So that I can share content on the platform

  @CreateArticle
  Scenario: Create, Verify, and Delete a New Article
    Given I navigate to the Conduit application
    When I click on "Sign In" link
    Then I should see the Login page
    
    When I login with email "pwtest@test.com" and password "Welcome2"
    Then I should be redirected to the Home page
    And I should see my username in the navigation bar

    When I click on "New Article"
    Then I should see the Article Editor page

    When I create a new article with random data
    Then I should be redirected to the Article Details page
    And I should see the correct article title and body
    And I should see "Edit Article" and "Delete Article" buttons
    And I should see the comments section

    When I go back to the Home page
    Then I should see the "Global Feed" tab active
    And the first article in the feed should match the created article

    When I click on the newly created article in the feed
    Then I should be redirected to the Article Details page

    When I click on "Delete Article"
    Then the article should be deleted
    And I should be redirected to the Home page
    And the deleted article should not be visible in the Global Feed
