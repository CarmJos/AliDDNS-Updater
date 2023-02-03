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

    protected final @NotNull String url;

    public WebhookNotify(@NotNull String url) {
        this.url = url;
    }

    public @NotNull String getParsedURL(@NotNull UpdateResult result) {
        return parseParams(result, this.url);
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
        if (url.replace(" ", "").length() == 0) return;
        HttpUtils.get(getParsedURL(result));
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("url", url);
        return values;
    }

    public static WebhookNotify parse(ConfigurationWrapper<?> conf) {
        return new WebhookNotify(conf.getString("url", ""));
    }

    public static WebhookNotify defaults(String url) {
        return new WebhookNotify(url);
    }

}
