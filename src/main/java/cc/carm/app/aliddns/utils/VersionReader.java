package cc.carm.app.aliddns.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class VersionReader {

    protected final @NotNull String versionsFileName;
    protected final @NotNull Properties properties;

    public VersionReader(@NotNull String versionsFileName) {
        this.versionsFileName = versionsFileName;
        this.properties = Optional.ofNullable(getProperties(versionsFileName)).orElse(new Properties());
    }

    public synchronized @NotNull String get(@NotNull String artifactID) {
        return get(artifactID, "UNKNOWN");
    }

    @Contract("_,!null->!null")
    public synchronized @Nullable String get(@NotNull String artifactID,
                                             @Nullable String defaultValue) {
        return this.properties.getProperty(artifactID, defaultValue);
    }

    protected Properties getProperties(@NotNull String versionsFileName) {
        try (InputStream is = this.getClass().getResourceAsStream("/" + versionsFileName)) {
            Properties p = new Properties();
            p.load(is);
            return p;
        } catch (Exception ignore) {
        }
        return null;
    }

}
