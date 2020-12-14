package com.carmwork.aliddns.managers;

import com.google.common.base.Charsets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class ConfigManager {

    private double configVersion = 1.6;

    private File sourceFile;
    private File dataFolder;
    private File configFile;

    private FileConfiguration newConfig = null;


    public ConfigManager() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        this.sourceFile = new File(path);


        this.dataFolder = sourceFile.getParentFile();

        this.configFile = new File(dataFolder, "config.yml");

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
            System.out.println("    Configuration found at " + configFile.getAbsolutePath());
        }
    }

    public void createConfig() {
        saveResource("config.yml", true);
        System.out.println("    Configuration created at " + configFile.getAbsolutePath());
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
            System.out.println("    Configuration backup at " + configFile.getAbsolutePath() + ".bak");
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
        return configVersion;
    }
}
