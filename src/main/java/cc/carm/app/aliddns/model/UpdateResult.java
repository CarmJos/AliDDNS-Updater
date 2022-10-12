package cc.carm.app.aliddns.model;

import cc.carm.app.aliddns.conf.AppConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpdateResult {

    boolean success;

    @NotNull String domain;
    @NotNull String type;

    @Nullable String beforeAddress;
    @Nullable String afterAddress;
    @Nullable String message;

    public UpdateResult(boolean success, @NotNull String domain, @NotNull String type,
                        @Nullable String beforeAddress, @Nullable String afterAddress,
                        @Nullable String message) {
        this.success = success;
        this.domain = domain;
        this.type = type;

        this.beforeAddress = beforeAddress;
        this.afterAddress = afterAddress;
        this.message = message;
    }

    public @NotNull String getType() {
        return type;
    }

    public @NotNull String getDomain() {
        return domain;
    }

    public boolean isUpdated() {
        return getBeforeAddress() != null && getAfterAddress() != null
                && !getBeforeAddress().equals(getAfterAddress());
    }

    public boolean hasRecordError() {
        return beforeAddress == null;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public @Nullable String getMessage() {
        return message;
    }

    public @Nullable String getAfterAddress() {
        return afterAddress;
    }

    public @Nullable String getBeforeAddress() {
        return beforeAddress;
    }

    public void executeWebhook() throws Exception {
        if (!isSuccess()) {
            AppConfig.WEBHOOK.ON_FAILED.getNotNull().execute(this);
        } else if (isUpdated()) {
            AppConfig.WEBHOOK.ON_SUCCESS.getNotNull().execute(this);
        } else {
            AppConfig.WEBHOOK.ON_UNCHANGED.getNotNull().execute(this);
        }
    }

}
