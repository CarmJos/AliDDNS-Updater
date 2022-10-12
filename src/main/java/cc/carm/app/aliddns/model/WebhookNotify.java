package cc.carm.app.aliddns.model;

import cc.carm.app.aliddns.utils.HttpUtils;
import cc.carm.lib.configuration.core.source.ConfigurationWrapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class WebhookNotify {

    protected final @Nullable String url;
    protected final @Nullable String body;
    protected final @Nullable String message;

    public WebhookNotify(@Nullable String url, @Nullable String requestBody, @Nullable String message) {
        this.url = url;
        this.body = requestBody;
        this.message = message;
    }

    public @Nullable String getParsedMessage(@NotNull UpdateResult result) {
        return parseParams(result, message);
    }

    public @Nullable String getParsedBody(@NotNull UpdateResult result) {
        String body = parseParams(result, this.body);
        if (body == null) return null;
        String message = getParsedMessage(result);
        return message == null ? body : body.replace("%(message)", message);
    }

    public @NotNull String getParsedURL(@NotNull UpdateResult result) {
        String url = parseParams(result, this.url);
        String message = getParsedMessage(result);
        return message == null ? url : url.replace("%(message)", message);
    }

    @Contract("_,null->null;_,!null->!null")
    protected String parseParams(@NotNull UpdateResult result, @Nullable String content) {
        if (content == null) return null;
        return content
                .replace("%(address)", Optional.ofNullable(result.getAfterAddress()).orElse("NULL"))
                .replace("%(domain)", result.getDomain())
                .replace("%(type)", result.getType());
    }

    public void execute(UpdateResult result) throws Exception {
        if (url == null || url.replace(" ", "").length() == 0) return;

        if (body == null) {
            HttpUtils.get(getParsedURL(result));
        } else {
            HttpUtils.post(getParsedURL(result), getParsedBody(result));
        }
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("url", url == null ? "" : url);
        if (body != null) values.put("body", body);
        if (message != null) values.put("message", message);
        return values;
    }

    public static WebhookNotify parse(ConfigurationWrapper<?> conf) {
        return new WebhookNotify(
                conf.getString("url", ""),
                conf.getString("body", null),
                conf.getString("message", null)
        );
    }

    public static WebhookNotify defaults(String message) {
        return new WebhookNotify("", "", message);
    }

}
