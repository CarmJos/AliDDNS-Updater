package cc.carm.app.aliddns;

import cc.carm.app.aliddns.conf.AppConfig;
import cc.carm.app.aliddns.conf.ServiceConfig;
import cc.carm.app.aliddns.manager.RequestManager;
import cc.carm.app.aliddns.utils.FileUtils;
import cc.carm.app.aliddns.utils.TimeDateUtils;
import cc.carm.app.aliddns.utils.VersionReader;
import cc.carm.lib.configuration.EasyConfiguration;
import cc.carm.lib.configuration.yaml.YAMLConfigProvider;
import cc.carm.lib.githubreleases4j.GithubReleases4J;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static RequestManager requestManager;

    public static void main(String[] args) throws InterruptedException {
        String currentVersion = new VersionReader("version.properties").get("version");

        print("-------------------------------------------");
        print("阿里云服务 DDNS更新器 (v" + currentVersion + ")");
        print("项目地址 https://git.carm.cc/AliDDNS-Updater");
        print("-------------------------------------------");
        Thread.sleep(1000L);

        print("初始化配置文件管理...");
        YAMLConfigProvider configuration = EasyConfiguration.from("config.yml");
        double configVersion = configuration.getConfiguration().getDouble("version", 1.0);
        if (configVersion < AppConfig.CURRENT_VERSION) {
            print("    配置文件过时，正在尝试重新创建...");
            try {
                FileUtils.copy(configuration.getFile(), "config-v" + configVersion + ".bak.yml");
                if (configuration.getFile().delete()) {
                    configuration = EasyConfiguration.from("config.yml");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        configuration.initialize(AppConfig.class);
        print("    完成加载配置文件 v" + AppConfig.CURRENT_VERSION);
        print();

        if (AppConfig.CHECK_UPDATE.getNotNull()) {
            print("正在检查更新(可以在配置文件中关闭此功能)... ");

            Integer behindVersions = GithubReleases4J.getVersionBehind("CarmJos", "AliDDNS-Updater", currentVersion);
            String downloadURL = GithubReleases4J.getReleasesURL("CarmJos", "AliDDNS-Updater");

            if (behindVersions == null) {
                print("    检查更新失败，请您定期查看插件是否更新，避免安全问题。");
                print("    下载地址 " + downloadURL);
            } else if (behindVersions == 0) {
                print("    检查完成，当前已是最新版本。");
            } else if (behindVersions > 0) {
                print("    发现新版本! 目前已落后 " + behindVersions + " 个版本。");
                print("    最新版下载地址 " + downloadURL);
            } else {
                print("    检查更新失败! 当前版本未知，请您使用原生版本以避免安全问题。");
                print("    最新版下载地址 " + downloadURL);
            }

            print();
        }

        print("初始化记录请求管理器...");
        requestManager = new RequestManager();
        int loaded = requestManager.getRequests().size();

        if (loaded < 1) {
            print("    您没有配置任何记录，请检查配置文件！");
            System.exit(0);
        } else {
            print("    初始化完成，共加载了 " + loaded + " 个任务");
        }

        print();
        print("所有任务已设定为每 " + TimeDateUtils.toDHMSStyle(ServiceConfig.PERIOD.getNotNull()) + " 进行一次更新。");
        print("-------------------------------------------");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getRequestManager().doUpdate();
            }
        }, 500, ServiceConfig.PERIOD.getNotNull() * 1000L);

    }


    public static void print(String... messages) {
        if (messages == null || messages.length == 0) {
            System.out.println(" ");
        } else {
            Arrays.stream(messages).forEach(System.out::println);
        }
    }

    public static void printWithPrefix(@NotNull String prefix, String... messages) {
        if (messages == null || messages.length == 0) {
            System.out.println(" ");
        } else {
            Arrays.stream(messages).map(x -> prefix + x).forEach(System.out::println);
        }
    }

    public static void info(String... messages) {
        printWithPrefix("[INFO] ", messages);
    }

    public static void debug(String... messages) {
        if (AppConfig.DEBUG.getNotNull()) printWithPrefix("[DEBUG] ", messages);
    }

    public static void severe(String... messages) {
        printWithPrefix("[ERROR] ", messages);
    }

    public static RequestManager getRequestManager() {
        return requestManager;
    }

}
