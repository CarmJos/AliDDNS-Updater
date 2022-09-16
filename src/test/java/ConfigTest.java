import cc.carm.app.aliddns.conf.AppConfig;
import cc.carm.lib.configuration.EasyConfiguration;
import cc.carm.lib.configuration.core.source.ConfigurationProvider;
import org.junit.Test;

public class ConfigTest {

    @Test
    public void onTest() {

        ConfigurationProvider<?> configuration = EasyConfiguration.from("target/example.yml");
        configuration.initialize(AppConfig.class);
        try {
            configuration.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
