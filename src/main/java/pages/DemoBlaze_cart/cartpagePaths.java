package pages.DemoBlaze_cart;

public class cartpagePaths {

    //    Main page locators
    public static final String LAPTOP_LIST_ITEMS = "//div[@id='tbodyid']/div";
    public static final String LAPTOP_ITEM_INDEX = "(//div[@id='tbodyid']//div)[%d]";
    public static final String LAPTOP_ITEM_OPTION = "(//div[@id='tbodyid']//div//h4/a)[%s]";


    //    product page locators
    public static final String PRODUCT_NAME_HEADER = "//div/h2[@class='name']";
    public static final String ADD_TO_CART_BUTTON = "//a[text()='Add to cart']";
    public static final String CART_PRODUCT_Details = "//tbody[@id='tbodyid']//tr/td[text()='%s']";

    //    cart page locators
    public static final String DELETE_BUTTON_BY_TEXT = "//a[text()='%s']";
    public static final String CART_ITEMS_ROWS = "//tbody[@id='tbodyid']//tr";

    //    generic locators
    public static final String aTExt = "//a[text()='%s']";
    public static final String NAV_CART = "//a[@id='cartur']";

}
