package cc.carm.app.aliddns.manager;

import cc.carm.app.aliddns.Main;
import cc.carm.app.aliddns.model.UpdateRequest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    public final String SECTION = "UpdateRequests";

    private int updatedTimes = 1;
    private boolean hasIPv6 = false;

    private final SimpleDateFormat format;

    public final HashMap<String, UpdateRequest> requests;

    public RequestManager() {
        this.requests = new HashMap<>();
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    }

    public int loadRequests() {
        requests.clear();
        Main.info("正在加载记录...");

        FileConfiguration config = Main.getConfigManager().getConfig();
        ConfigurationSection rootSection = config.getConfigurationSection(SECTION);
        if (rootSection != null) {

            for (String taskName : rootSection.getKeys(false)) {
                ConfigurationSection requestSection = rootSection.getConfigurationSection(taskName);
                if (requestSection == null) continue;
                UpdateRequest request = UpdateRequest.readConfiguration(requestSection);
                if (request.isIpv6() && !ConfigManager.isIPV6Enabled()) {
                    Main.info("记录 [" + taskName + "] 为IPv6任务，但本实例未启用IPv6，跳过加载。");
                    continue;
                }
                requests.put(taskName, request);
            }
        }

        this.hasIPv6 = getRequests().values().stream().anyMatch(UpdateRequest::isIpv6);
        this.updatedTimes = 1;

        return getRequests().size();
    }


    public void doUpdate() {

        Main.info("[" + this.format.format(new Date()) + "]" + " 开始执行第" + updatedTimes + "次更新...");

        Main.info("开始从 " + ConfigManager.getIPv4QueryURL() + " 获取IPv4地址...");
        String IPv4 = getCurrentHostIP(false);
        Main.info("     获取完成，当前IPv4地址为 " + IPv4);

        String IPv6 = null;
        if (ConfigManager.isIPV6Enabled() && hasIPv6) {
            Main.info("开始从 " + ConfigManager.getIPv6QueryURL() + " 获取IPv6地址...");
            IPv6 = getCurrentHostIP(true);
            Main.info("     获取完成，当前IPv6地址为 " + IPv6);
        }

        for (Map.Entry<String, UpdateRequest> entry : getRequests().entrySet()) {
            UpdateRequest currentRequest = entry.getValue();
            if (currentRequest.isIpv6() && IPv6 == null) {
                Main.info("记录 [" + entry.getKey() + "] 为IPv6任务，但本实例未启用IPv6，跳过。");
                continue;
            }
            try {

                String currentHost = currentRequest.isIpv6() ? IPv6 : IPv4;

                if (currentRequest.shouldUpdate(currentHost)) {
                    boolean success = currentRequest.doUpdate(currentHost);
                    if (success) {
                        Main.info("     记录 “" + currentRequest.getRecord() + "." + currentRequest.getDomain() + "” 成功更新为 " + currentHost + " 。");
                    } else {
                        Main.error("    记录 “" + currentRequest.getRecord() + "." + currentRequest.getDomain() + "” 更新失败,请检查网络。");
                    }
                } else {
                    Main.info("     记录 “" + currentRequest.getRecord() + "." + currentRequest.getDomain() + "” 无需更新，跳过。");
                }
            } catch (Exception exception) {
                Main.error("在更新请求 [" + entry.getKey() + "] 时发生问题，请检查配置。");
                exception.printStackTrace();
            }
        }

        updatedTimes++;
    }

    public HashMap<String, UpdateRequest> getRequests() {
        return new HashMap<>(this.requests);
    }

    public static String getCurrentHostIP(boolean isIPV6) {
        StringBuilder result = new StringBuilder();
        String requestURL = isIPV6 ? ConfigManager.getIPv6QueryURL() : ConfigManager.getIPv4QueryURL();

        try {
            // 使用HttpURLConnection网络请求第三方接口
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            urlConnection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
        } catch (Exception e) {
            Main.error("获取" + (isIPV6 ? "IPV6" : "IPV4") + "地址失败，请检查配置的请求连接和当前网络！");
            e.printStackTrace();
        }
        return result.toString();

    }

}
