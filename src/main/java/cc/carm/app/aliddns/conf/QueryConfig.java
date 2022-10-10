package cc.carm.app.aliddns.conf;

import cc.carm.lib.configuration.core.ConfigurationRoot;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.ConfigValue;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;

public class QueryConfig extends ConfigurationRoot {

    @HeaderComment({"IPv4地址获取链接", "如不需要更新IPv4域名，则可以直接将地址留空。"})
    public static final ConfigValue<String> V4 = ConfiguredValue.of(String.class, "http://ifconfig.me/ip");

    @HeaderComment({"IPv6地址获取链接 (可选)", "如不需要更新IPV6域名，则可以直接将地址留空。"})
    public static final ConfigValue<String> V6 = ConfiguredValue.of(String.class, "https://v6.ip.zxinc.org/getip");

}
