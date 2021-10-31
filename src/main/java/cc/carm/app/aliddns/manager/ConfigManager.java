package cc.carm.app.aliddns.manager;

import com.google.common.base.Charsets;
import com.sun.istack.internal.Nullable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ConfigManager {

    private static ConfigManager instance;

    private final double CONFIG_VERSION = 1.7;

    private File sourceFile;
    private File dataFolder;
    private File configFile;

    private FileConfiguration newConfig = null;


    public ConfigManager() {
        instance = this;

        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        this.sourceFile = new File(path);
        this.dataFolder = sourceFile.getParentFile();
        this.configFile = new File(dataFolder, "config.yml");

    }

    public static boolean isDebugMode() {
        return true;
    }

    public static String getRegionID() {
        return getInstance().getConfig().getString("Service.region-id", "cn-hangzhou");
    }

    public static long getPeriod() {
        return getInstance().getConfig().getLong("Service.period", 900000L);
    }

    public static String getIPv4QueryURL() {
        return getInstance().getConfig().getString("Service.ipQuery.IPv4", "http://ifconfig.me/ip");
    }

    @Nullable
    public static String getIPv6QueryURL() {
        return getInstance().getConfig().getString("Service.ipQuery.IPv6", "https://v6.ip.zxinc.org/getip");
    }

    public static boolean isIPV6Enabled() {
        return getIPv6QueryURL() == null || getIPv4QueryURL().length() == 0;
    }

    public static ConfigManager getInstance() {
        return instance;
    }

    public FileConfiguration getConfig() {
        if (newConfig == null) {
            reloadConfig();
        }
        return newConfig;
    }

    public void reloadConfig() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = getResource("config.yml");
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        newConfig.setDefaults(defConfig);
    }

    public void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            System.out.print("Could not save config to " + configFile);
        }
    }

    public void initConfig() {
        if (!configFile.exists()) {
            createConfig();
        } else {
            System.out.println("    配置文件加载于 " + configFile.getAbsolutePath());
        }
    }

    public void createConfig() {
        saveResource("config.yml", true);
        System.out.println("    配置文件创建于 " + configFile.getAbsolutePath());
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + sourceFile);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                System.out.print("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            System.out.print("Could not save " + outFile.getName() + " to " + outFile);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void backupConfig() {
        try {
            copy(configFile, "config.yml.bak");
            System.out.println("    旧的配置文件已备份与 " + configFile.getAbsolutePath() + ".bak");
            this.configFile = new File(dataFolder, "config.yml");
        } catch (Exception ignore) {

        }

    }

    /**
     * Rename the file.
     */
    public static void copy(final File file, final String newName) throws IOException {
        // file is null then return false
        if (file == null) return;
        // file doesn't exist then return false
        if (!file.exists()) return;
        // the new name equals old name then return true
        if (newName.equals(file.getName())) return;
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        if (newFile.exists()) {
            newFile.delete();
        }
        try (InputStream input = new FileInputStream(file); OutputStream output = new FileOutputStream(newFile)) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }
    }

    public double getConfigVersion() {
        return CONFIG_VERSION;
    }


}
