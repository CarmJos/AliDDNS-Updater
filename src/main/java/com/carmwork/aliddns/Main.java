package com.carmwork.aliddns;

import com.carmwork.aliddns.enums.RecordType;
import com.carmwork.aliddns.managers.ConfigManager;
import com.carmwork.aliddns.managers.DNSManager;
import com.carmwork.aliddns.managers.RequestManager;
import com.carmwork.aliddns.model.UpdateRequest;
import com.carmwork.aliddns.utils.IPAddressUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static ConfigManager configManager;
    private static RequestManager requestManager;

    private static TimerTask updateTimerTask;

    private static boolean outputDomainRecordsResponse = false;
    private static boolean enableV6 = true;

    public static void main(String[] args) {
        System.out.println("--------------------------------------");
        System.out.println("Aliyun DDNS Updater");
        System.out.println("--------------------------------------");

        System.out.println("Initializing ConfigManager...");
        configManager = new ConfigManager();
        getConfigManager().initConfig();

        if (getConfigManager().getConfig().getDouble("version", 1.0) < getConfigManager().getConfigVersion()) {
            System.out.println("    Configuration outdated , try recreating. ");
            getConfigManager().backupConfig();
            getConfigManager().createConfig();
        }

        System.out.println("    Loaded Configuration v" + getConfigManager().getConfig().getDouble("version", 1.0));

        outputDomainRecordsResponse = getConfigManager().getConfig().getBoolean("outputDomainRecordsResponse", false);
        if (outputDomainRecordsResponse) {
            System.out.println("Domain Records Response Output enabled.");
        }

        enableV6 = (getConfigManager().getConfig().getString("IPQueryURL.IPv6") != null) && (!getConfigManager().getConfig().getString("IPQueryURL.IPv6").equalsIgnoreCase(""));
        if (!enableV6) {
            System.out.println("IPv6 DDNS disabled.");
        }

        System.out.println("");

        System.out.println("Initializing RequestManager...");
        requestManager = new RequestManager();
        requestManager.loadRequests();

        if (getRequestManager().getRequests().size() < 1) {
            System.err.println("No configured requests , exiting.");
            System.exit(0);
        }


        System.out.println("");

        long period = getConfigManager().getConfig().getLong("period", 500);
        System.out.println("All the tasks will be updated every " + period + " ms.");

        System.out.println("");

        System.out.println("Starting update Thread...");
        System.out.println("--------------------------------------");

        Timer timer = new Timer();
        timer.schedule(updateTimerTask = new TimerTask() {
            int i = 1;

            @Override
            public void run() {
                System.out.println("#" + i + " " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                System.out.println("    Getting current IPv4 Address ...");
                String IPv4 = IPAddressUtil.getCurrentHostIP(RecordType.A);
                System.out.println("      Current IPv4 : " + IPv4);

                String IPv6 = null;
                if (enableV6) {
                    System.out.println("    Getting current IPv6 Address ...");
                    IPv6 = IPAddressUtil.getCurrentHostIP(RecordType.AAAA);
                    System.out.println("      Current IPv6 : " + IPv6);
                }


                for (String requestName : getRequestManager().getRequests().keySet()) {
                    System.out.println("    Running Request [" + requestName + "]");
                    UpdateRequest currentRequest = getRequestManager().getRequests().get(requestName);
                    if (currentRequest.getType() == RecordType.A) {
                        DNSManager.updateDNS(IPv4, currentRequest);
                    } else {
                        if (!enableV6) {
                            System.out.println("      Skipped(IPv6 disabled).");
                        } else {
                            DNSManager.updateDNS(IPv6, currentRequest);
                        }
                    }
                }
                i++;

            }
        }, 500, period);
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

    public static boolean isEnableV6() {
        return enableV6;
    }

    public static boolean isOutputDomainRecordsResponse() {
        return outputDomainRecordsResponse;
    }
}
