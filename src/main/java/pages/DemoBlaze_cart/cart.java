package pages.DemoBlaze_cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microsoft.playwright.options.LoadState;
import config.Settings;
import utilze.playwright;

public class cart {
    private static final Logger logger = LoggerFactory.getLogger(cart.class);
    playwright pm = new playwright();

    private static final String PRODUCT_NAME_KEY = "productName";
    private static final String CART_PAGE_URL = "https://www.demoblaze.com/cart.html";
    private String lastDialogMessage = "";

    public void IsHomepageAppeared() {
        pm.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
        pm.assertPageHasURL(Settings.Url);
    }

    public void ChoosingCategory(String category) {
        pm.click(String.format(cartpagePaths.aTExt, category));
    }

    public void verifyLaptopListDisplayed() {
        // Use the first item to verify list visibility to avoid strict mode violation (multiple elements)
        // Corrected static access
        String firstItem = String.format(cartpagePaths.LAPTOP_ITEM_INDEX, 1);
        pm.waitForElementVisibility(firstItem);
        pm.assertVisible(firstItem);
    }

    public void clickLaptopByPosition(String position) {
        int index = switch (position.toLowerCase()) {
            case "first" -> 1;
            case "second" -> 2;
            case "third" -> 3;
            default -> 1; // Default to first if unknown
        };
        
        String locator = String.format(cartpagePaths.LAPTOP_ITEM_OPTION, index);
        pm.click(locator);
    }

    public void verifyLandedOnProductPage() {
        pm.waitForElementVisibility(cartpagePaths.ADD_TO_CART_BUTTON);
        pm.assertVisible(cartpagePaths.PRODUCT_NAME_HEADER);
    }

    public void memorizeProductDetails() {
        String product = pm.getText(cartpagePaths.PRODUCT_NAME_HEADER);
        pm.setVariable(PRODUCT_NAME_KEY, product);
        logger.info("Memorized product: {}", product);
    }

    public void clickAddToCart() {
        // Register listener BEFORE action
        pm.getPage().onDialog(dialog -> {
            logger.info("Dialog appeared: {}", dialog.message());
            lastDialogMessage = dialog.message();
            dialog.accept();
        });
        pm.click(cartpagePaths.ADD_TO_CART_BUTTON);
    }

    public void verifyPopupText(String expectedText) {
        // Poll for message
        for (int i = 0; i < 10; i++) {
             if (lastDialogMessage != null && !lastDialogMessage.isEmpty()) {
                 break;
             }
             pm.waitForTimeout(500);
        }

       if (lastDialogMessage == null || lastDialogMessage.isEmpty()) {
            throw new AssertionError("No popup dialog was captured.");
       }
       if (!lastDialogMessage.equals(expectedText)) {
            throw new AssertionError(String.format("Expected popup text '%s' but got '%s'", expectedText, lastDialogMessage));
       }
       logger.info("Verified popup text: {}", expectedText);
    }

    public void clickCartOption() {
        pm.click(cartpagePaths.NAV_CART);
    }

    public void verifyCartPage() {
        pm.assertPageHasURL(CART_PAGE_URL);
        pm.waitForElementVisibility(cartpagePaths.CART_ITEMS_ROWS);
    }

    public void verifyMemorizedProductInCart() {
        String memorizedProduct = pm.getVariableAsString(PRODUCT_NAME_KEY);
        String locator = String.format(cartpagePaths.CART_PRODUCT_Details, memorizedProduct);
        pm.waitForElementVisibility(locator);
        pm.assertVisible(locator);
    }

    public void clickDeleteProduct() {
        String memorizedProduct = pm.getVariableAsString(PRODUCT_NAME_KEY);
        String rowLocator = String.format(cartpagePaths.CART_PRODUCT_Details, memorizedProduct);
        String deleteLocator = rowLocator + "/..//a[text()='Delete']";
        pm.click(deleteLocator);
    }

    public void verifyProductRemoved() {
        String memorizedProduct = pm.getVariableAsString(PRODUCT_NAME_KEY);
        String locator = String.format(cartpagePaths.CART_PRODUCT_Details, memorizedProduct);
        pm.waitForElementInvisibility(locator);
        pm.assertIsNotVisible(locator);
    }

    public void verifyCartEmpty() {
        // If we expect NO products
        pm.assertIsNotVisible(cartpagePaths.CART_ITEMS_ROWS);
    }}
