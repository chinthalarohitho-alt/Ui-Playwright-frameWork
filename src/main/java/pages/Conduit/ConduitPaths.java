package pages.Conduit;

/**
 * Locator paths for Conduit application.
 * Identifies elements for Login, Article creation, reading, and deletion.
 */
public class ConduitPaths {
    
    // Base URL
    public static final String BASE_URL = "https://conduit.bondaracademy.com/";

    // Navigation
    public static final String SIGN_IN_LINK = "//a[@href='/login']";
    public static final String HOME_LINK = "//a[contains(@class, 'nav-link') and contains(text(), 'Home')]";
    public static final String UNORDERED_LIST_FOR_NEW_ARTICLE = "//ul//li//a[@href='/editor']";
    public static final String NEW_ARTICLE_LINK = "//a[@href='/editor']";
    public static final String USERNAME_LINK = "//ul[contains(@class, 'navbar-nav')]//a[contains(@href, '/profile/')]";

    // Login Page
    public static final String EMAIL_INPUT = "//input[@placeholder='Email']";
    public static final String PASSWORD_INPUT = "//input[@placeholder='Password']";
    public static final String SIGN_IN_BUTTON = "//button[@type='submit']";

    // Article Editor
    public static final String TITLE_INPUT = "//input[@placeholder='Article Title']";
    public static final String DESCRIPTION_INPUT = "//input[contains(@placeholder, 'article about?')]";
    public static final String BODY_INPUT = "//textarea[contains(@placeholder, 'markdown')]";
    public static final String TAGS_INPUT = "//input[@placeholder='Enter tags']";
    public static final String PUBLISH_BUTTON = "//button[contains(text(), 'Publish Article')]";

    // Article Details
    public static final String ARTICLE_TITLE = "//h1";
    public static final String ARTICLE_BODY = "//div[contains(@class, 'article-content')]";
    public static final String EDIT_ARTICLE_BUTTON = "(//a[contains(text(), 'Edit Article')])[1]";
    public static final String DELETE_ARTICLE_BUTTON = "(//button[contains(text(), 'Delete Article')])[1]";
    public static final String COMMENT_SECTION = "//textarea[@placeholder='Write a comment...']";

    // Home / Global Feed
    public static final String GLOBAL_FEED_TAB = "//a[contains(text(), 'Global Feed')]";
    // First article in the list
    public static final String FIRST_ARTICLE_LINK = "(//div[@class='article-preview'])[1]//a[@class='preview-link']";
    public static final String FIRST_ARTICLE_TITLE = "(//div[@class='article-preview'])[1]//h1";
    
    // Validation
    public static final String ACTIVE_TAB_CLASS = "active";
}
