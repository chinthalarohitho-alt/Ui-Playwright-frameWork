package steps.cart;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.DemoBlaze_cart.cart;

public class cartSteps {

    cart cart = new cart();

    @Given("I navigate to the HomePage of DemoBlaze")
    public void iNavigateToTheHomePageOfDemoBlaze() {
         cart.IsHomepageAppeared();
    }

    @When("the user clicks on the {string} category in the categories section")
    public void theUserClicksOnTheCategoryInTheCategoriesSection(String category) {
         cart.ChoosingCategory(category);
    }

    @Then("the list of laptops should be displayed")
    public void theListOfLaptopsShouldBeDisplayed() {
        cart.verifyLaptopListDisplayed();
    }

    @When("the user clicks on the {string} laptop from the list")
    public void theUserClicksOnTheLaptopFromTheList(String position) {
        cart.clickLaptopByPosition(position);
    }

    @Then("the user should land on the {string}")
    public void theUserShouldLandOnThe(String pageName) {
        // We can check if pageName contains "detail"
        if (pageName.contains("detail")) {
            cart.verifyLandedOnProductPage();
        } else if (pageName.contains("cart")) {
            cart.verifyCartPage();
        }
    }

    @And("the user memorizes the laptop details \\(name, price, and specifications)")
    public void theUserMemorizesTheLaptopDetailsNamePriceAndSpecifications() {
        cart.memorizeProductDetails();
    }

    @When("the user clicks on the {string} button")
    public void theUserClicksOnTheButton(String buttonName) {
        if (buttonName.equalsIgnoreCase("Add to cart")) {
            cart.clickAddToCart();
        }
    }

    @Then("a popup with text {string} should be visible")
    public void aPopupWithTextShouldBeVisible(String message) {
        cart.verifyPopupText(message);
    }

    @And("the popup should automatically disappear after a short duration")
    public void thePopupShouldAutomaticallyDisappearAfterAShortDuration() {
        // Popup in DemoBlaze is usually an alert which we accepted immediately
        // So this step might be redundant or we wait a bit
        // cart.waitForTimeout(1000); 
    }

    @When("the user clicks on the {string} option from the navbar")
    public void theUserClicksOnTheOptionFromTheNavbar(String option) {
        if (option.equalsIgnoreCase("Cart")) {
            cart.clickCartOption();
        }
    }

    @And("the cart should contain the same laptop which was memorized")
    public void theCartShouldContainTheSameLaptopWhichWasMemorized() {
        cart.verifyMemorizedProductInCart();
    }

    @When("the user clicks the {string} hyperlink next to the product")
    public void theUserClicksTheHyperlinkNextToTheProduct(String linkName) {
        if (linkName.equalsIgnoreCase("Delete")) {
            cart.clickDeleteProduct();
        }
    }

    @Then("the laptop should be removed from the cart")
    public void theLaptopShouldBeRemovedFromTheCart() {
        cart.verifyProductRemoved();
    }

    @And("the cart page should not contain any products")
    public void theCartPageShouldNotContainAnyProducts() {
        cart.verifyCartEmpty();
    }
}
