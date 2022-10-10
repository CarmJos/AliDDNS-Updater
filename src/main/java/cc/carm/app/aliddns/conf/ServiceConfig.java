package cc.carm.app.aliddns.conf;

import cc.carm.lib.configuration.core.ConfigurationRoot;
import cc.carm.lib.configuration.core.annotation.HeaderComment;
import cc.carm.lib.configuration.core.value.ConfigValue;
import cc.carm.lib.configuration.core.value.type.ConfiguredValue;

public class ServiceConfig extends ConfigurationRoot {

    @HeaderComment("更新间隔,单位为秒。")
    public static final ConfigValue<Integer> PERIOD = ConfiguredValue.of(Integer.class, 900);

    @HeaderComment("运行的次数。小于等于0代表循环运行，程序将在达到预定次数后自动退出。")
    public static final ConfigValue<Integer> TIMES = ConfiguredValue.of(Integer.class, -1);

    @HeaderComment({"阿里云接口地域ID，可以不改动。", "请参考 https://help.aliyun.com/knowledge_detail/40654.html"})
    public static final ConfigValue<String> REGION_ID = ConfiguredValue.of(String.class, "cn-hangzhou");


}
