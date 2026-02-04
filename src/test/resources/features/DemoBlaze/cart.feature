@cart @ui @regression
Feature: Forms Page End-to-End Validation
# -----------------------------------------------------------------
# Background
# -----------------------------------------------------------------
  Background: Logging and navigating to the Form Tab
    Given I navigate to the HomePage of DemoBlaze

# -----------------------------------------------------------------
#          cart Test cases
# -----------------------------------------------------------------

  @removingCart
  Scenario: User adds a laptop to the cart and removes it successfully
    When the user clicks on the "Phones" category in the categories section
    Then the list of laptops should be displayed

    When the user clicks on the "third" laptop from the list
    Then the user should land on the "Phones detail page"
    And the user memorizes the laptop details (name, price, and specifications)

    When the user clicks on the "Add to cart" button
    Then a popup with text "Product added" should be visible
    And the popup should automatically disappear after a short duration

    When the user clicks on the "Cart" option from the navbar
    Then the user should land on the "cart page"
    And the cart should contain the same laptop which was memorized

    When the user clicks the "Delete" hyperlink next to the product
    Then the laptop should be removed from the cart
    And the cart page should not contain any products

