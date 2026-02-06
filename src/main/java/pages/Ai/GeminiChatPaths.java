package pages.Ai;

public class GeminiChatPaths {
    
    public static final String NEW_CHAT_BUTTON_ARIA_LABEL = "New chat";
    public static final String NEW_CHAT_BUTTON_XPATH = "//a[@aria-label='New chat']";
    
    public static final String PROMPT_INPUT_ARIA_LABEL = "Enter a prompt here";
    public static final String PROMPT_INPUT_XPATH = "//div[@role='textbox'][@aria-label='Enter a prompt here']";
    
    public static final String SEND_BUTTON_ARIA_LABEL = "Send message";
    public static final String SEND_BUTTON_XPATH = "//button[@aria-label='Send message']";
    
    public static final String USER_MESSAGE_CONTAINER = "//user-query-content";
    
    public static final String AI_RESPONSE_CONTAINER = "//response-container";
    
    public static final String MESSAGE_CONTENT = "//response-container//div[contains(@class, 'markdown')]";
    
    public static final String CHAT_HISTORY = "//infinite-scroller[contains(@class, 'chat-history')]";
    
    public static final String CHAT_LIST_ITEMS = "//mat-list-item";
    public static final String ACTIVE_CHAT = "//mat-list-item[contains(@class, 'active')]";
    
    public static final String RESPONSE_LOADING = "//response-container[contains(@class, 'ng-animating')]";
    
    public static final String STOP_RESPONSE_BUTTON = "//button[@aria-label='Stop response']";
}
