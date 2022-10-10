package cc.carm.app.aliddns.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WebhookNotify {

    protected final @NotNull String url;
    protected final @Nullable String requestBody;
    protected final @Nullable String message;

    public WebhookNotify(@NotNull String url, @Nullable String requestBody, @Nullable String message) {
        this.url = url;
        this.requestBody = requestBody;
        this.message = message;
    }


    public void execute() {

    }


}
