package cc.carm.app.aliddns;

import cc.carm.app.aliddns.manager.ConfigManager;
import cc.carm.app.aliddns.manager.RequestManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static ConfigManager configManager;
    private static RequestManager requestManager;

    private static TimerTask updateTimerTask;

    public static void main(String[] args) throws InterruptedException {
        print("-------------------------------------------");
        print("阿里云服务 DDNS更新器");
        print("项目地址 https://git.carm.cc/AliDDNS-Updater");
        print("-------------------------------------------");
        Thread.sleep(1000L);
        print("初始化配置文件管理...");

        configManager = new ConfigManager();
        getConfigManager().initConfig();

        if (getConfigManager().getConfig().getDouble("version", 1.0) < getConfigManager().getConfigVersion()) {
            print("    配置文件过时，正在尝试重新创建...");
            getConfigManager().backupConfig();
            getConfigManager().createConfig();
        }

        print("    完成加载配置文件 v" + getConfigManager().getConfig().getDouble("version", 1.0));

        print();

        print("初始化记录请求管理器...");
        requestManager = new RequestManager();
        int loaded = requestManager.loadRequests();

        if (loaded < 1) {
            print("    您没有配置任何记录，请检查配置文件！");
            System.exit(0);
        } else {
            print("    初始化完成，共加载了 " + loaded + " 个任务");
        }

        print();
        print("所有任务已设定为每 " + ConfigManager.getPeriod() + " 毫秒进行一次更新。");
        print("-------------------------------------------");

        Timer timer = new Timer();
        timer.schedule(updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                getRequestManager().doUpdate();
            }
        }, 500, ConfigManager.getPeriod());

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
        printWithPrefix("[DEBUG] ", messages);
    }

    public static void error(String... messages) {
        printWithPrefix("[ERROR] ", messages);
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static RequestManager getRequestManager() {
        return requestManager;
    }

    public static TimerTask getUpdateTimerTask() {
        return updateTimerTask;
    }

}
