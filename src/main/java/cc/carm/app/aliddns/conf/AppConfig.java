package cc.carm.app.aliddns.conf;

import cc.carm.app.aliddns.model.RequestRegistry;
import cc.carm.app.aliddns.model.WebhookNotify;
import cc.carm.lib.configuration.core.ConfigurationRoot;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.ConfigValue;
import cc.carm.lib.configuration.core.value.type.ConfiguredSection;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;

@SuppressWarnings("unused")
public class AppConfig extends ConfigurationRoot {

    public static final double CURRENT_VERSION = 2.0;

    @HeaderComment("配置文件版本，请不要修改。")
    public static final ConfigValue<Double> VERSION = ConfiguredValue.of(Double.class, 2.0D);

    @HeaderComment("是否输出域名记录查询返回信息。(用于调试)")
    public static final ConfigValue<Boolean> DEBUG = ConfiguredValue.of(Boolean.class, false);

    @HeaderComment("是否检查本程序更新。(默认开启)")
    public static final ConfigValue<Boolean> CHECK_UPDATE = ConfiguredValue.of(Boolean.class, true);

    public static final Class<?> SERVICE = ServiceConfig.class;
    @HeaderComment({"", "本机IP查询接口配置。", "用于获取对应的IP地址，以更新到域名记录。"})
    public static final Class<?> QUERY = QueryConfig.class;

    @HeaderComment({
            "", "更新任务配置。",
            "具体配置请参考 https://github.com/CarmJos/AliDDNS-Updater/blob/master/.doc/REQUEST.md",
    })
    public static final ConfigValue<RequestRegistry> REQUESTS = ConfiguredSection.builder(RequestRegistry.class)
            .parseValue((w, d) -> RequestRegistry.loadFrom(w))
            .serializeValue(RequestRegistry::serialize)
            .defaults(RequestRegistry.defaults())
            .build();

    @HeaderComment({
            "", "更新通知(Webhook) 配置",
            "具体配置请参考 https://github.com/CarmJos/AliDDNS-Updater/blob/master/.doc/WEBHOOK.md"
    })
    public static final class WEBHOOK {

        @HeaderComment("当域名成功更新时执行的Webhook通知")
        public static final ConfigValue<WebhookNotify> ON_SUCCESS = ConfiguredValue
                .builder(WebhookNotify.class).fromSection()
                .parseValue((v, d) -> WebhookNotify.parse(v))
                .serializeValue(WebhookNotify::serialize)
                .defaults(WebhookNotify.defaults("更新%(type)域名 %(domain) 完成，新的IP地址为 %(address)。"))
                .build();

        @HeaderComment("当域名更新失败/出错时执行的Webhook通知")
        public static final ConfigValue<WebhookNotify> ON_FAILED = ConfiguredValue
                .builder(WebhookNotify.class).fromSection()
                .parseValue((v, d) -> WebhookNotify.parse(v))
                .serializeValue(WebhookNotify::serialize)
                .defaults(WebhookNotify.defaults("更新域名 %(domain) 失败，请检查控制台查看错误原因。"))
                .build();

        @HeaderComment("当域名无需更新时执行的Webhook通知")
        public static final ConfigValue<WebhookNotify> ON_UNCHANGED = ConfiguredValue
                .builder(WebhookNotify.class).fromSection()
                .parseValue((v, d) -> WebhookNotify.parse(v))
                .serializeValue(WebhookNotify::serialize)
                .defaults(WebhookNotify.defaults("域名 %(domain) 地址未变更，跳过更新。"))
                .build();
    }

}
