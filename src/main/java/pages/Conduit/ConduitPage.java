package pages.Conduit;

import utilze.playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Conduit application.
 * Handles interactions for Login, Article Management, and Navigation.
 */
public class ConduitPage {
    private static final Logger logger = LoggerFactory.getLogger(ConduitPage.class);
    playwright pm = new playwright();

    // Navigation Methods
    public void navigateToHome() {
        logger.info("Navigating to Home Page: {}", ConduitPaths.BASE_URL);
        pm.navigateTo(ConduitPaths.BASE_URL);
        pm.waitForDOMContentLoaded();
        // Wait for potential redirect or load
    }

    public void clickSignInLink() {
        logger.info("Clicking Sign In link");
        pm.click(ConduitPaths.SIGN_IN_LINK);
        pm.waitForElementVisibility(ConduitPaths.EMAIL_INPUT);
    }

    public void clickNewArticle() {
        logger.info("Clicking New Article link");
        pm.click(ConduitPaths.NEW_ARTICLE_LINK); 
        pm.waitForElementVisibility(ConduitPaths.TITLE_INPUT);
    }

    public void clickHome() {
        logger.info("Clicking Home link");
        pm.click(ConduitPaths.HOME_LINK);
        pm.waitForElementVisibility(ConduitPaths.GLOBAL_FEED_TAB);
    }

    // Login Methods
    public void login(String email, String password) {
        logger.info("Logging in with user: {}", email);
        // Using fill ensuring we clear and type
        pm.fill(ConduitPaths.EMAIL_INPUT, email);
        pm.fill(ConduitPaths.PASSWORD_INPUT, password);
        pm.click(ConduitPaths.SIGN_IN_BUTTON);
        
        // Critical: Wait for login to complete (username visibility)
        try {
            pm.waitForElementVisibility(ConduitPaths.USERNAME_LINK);
            logger.info("Login Successful");
        } catch (Exception e) {
            logger.error("Login verification timed out in login method", e);
            // We don't throw yet, let validation step fail if needed
        }
    }

    public boolean isLoggedIn() {
        logger.info("Checking if user is logged in...");
        try {
            // Already waited in login(), but double check logic
            return pm.isVisibleSafe(ConduitPaths.USERNAME_LINK);
        } catch (Exception e) {
            return false;
        }
    }

    // Article Methods
    public void createArticle(String title, String description, String body, String tags) {
        logger.info("Creating article with Title: {}", title);
        pm.fill(ConduitPaths.TITLE_INPUT, title);
        pm.fill(ConduitPaths.DESCRIPTION_INPUT, description);
        pm.fill(ConduitPaths.BODY_INPUT, body);
        pm.fill(ConduitPaths.TAGS_INPUT, tags);
        pm.click(ConduitPaths.PUBLISH_BUTTON);
        pm.waitForDOMContentLoaded();
    }

    public void verifyArticleDetailsOpen() {
        logger.info("Verifying Article Details page is open");
        pm.waitForElementVisibility(ConduitPaths.DELETE_ARTICLE_BUTTON);
        pm.waitForElementVisibility(ConduitPaths.EDIT_ARTICLE_BUTTON);
    }

    public String getArticleTitle() {
        pm.waitForElementVisibility(ConduitPaths.ARTICLE_TITLE);
        return pm.getTextSafe(ConduitPaths.ARTICLE_TITLE);
    }

    public void deleteArticle() {
        logger.info("Deleting article...");
        pm.getPage().onDialog(dialog -> {
            logger.info("Accepting dialog: {}", dialog.message());
            dialog.accept();
        });
        
        pm.click(ConduitPaths.DELETE_ARTICLE_BUTTON);
        pm.waitForElementVisibility(ConduitPaths.GLOBAL_FEED_TAB);
    }

    // Feed Methods
    public boolean isGlobalFeedActive() {
        try {
            String classes = pm.getPage().locator(ConduitPaths.GLOBAL_FEED_TAB).getAttribute("class");
            return classes != null && classes.contains("active");
        } catch (Exception e) {
            return false;
        }
    }

    public String getFirstArticleTitleInFeed() {
        logger.info("Getting first article title from feed");
        pm.waitForElementVisibility(ConduitPaths.FIRST_ARTICLE_LINK);
        return pm.getTextSafe(ConduitPaths.FIRST_ARTICLE_TITLE);
    }

    public void clickFirstArticle() {
        logger.info("Clicking first article in feed");
        pm.click(ConduitPaths.FIRST_ARTICLE_TITLE);
        pm.waitForElementVisibility(ConduitPaths.ARTICLE_TITLE);
    }
}
