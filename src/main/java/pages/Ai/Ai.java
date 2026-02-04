package pages.Ai;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import config.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.common.GlobalPaths;
import utilze.playwright;

public class Ai {
    private static final Logger logger = LoggerFactory.getLogger(Ai.class);
    playwright pm = new playwright();
    GlobalPaths paths = new GlobalPaths();
    protected static Page newTab1;

    public void LandOnChatgpt() {
        pm.navigateTo(Settings.chatGptUrl);
        pm.getPage().waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    public void configureChatGpt(String params) {
        String FilePath = String.valueOf(pm.getFilePath("configureChatGpt"));
        String prompt = pm.readTextFile(FilePath);

        if(params.startsWith("Accuracy")){
            pm.click(".placeholder");
            pm.fill(".placeholder",prompt);
            pm.click(paths.dataAttr("button","testid","send-button"));
            pm.waitForTimeout(5000);
        }
    }

    public void openGeminiInNewTab() {
        try {
             newTab1 = pm.getContext().waitForPage(() -> {
                pm.getPage().evaluate("window.open('"+Settings.Url+"')");
            });

            newTab1.waitForLoadState(LoadState.DOMCONTENTLOADED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void inputToGemini(String input) {
        pm.click(AiPAths.geminiInput);
        pm.fill(AiPAths.geminiInput,input);
        pm.waitForElementVisibility(AiPAths.geminiSendICon);
        pm.click(AiPAths.geminiSendICon);
    }

    public void userCaptureResponse() {
        //p[@data-path-to-node]
    }
}
