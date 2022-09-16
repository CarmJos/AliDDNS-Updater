package cc.carm.app.aliddns.manager;

import cc.carm.app.aliddns.Main;
import cc.carm.app.aliddns.conf.AppConfig;
import cc.carm.app.aliddns.conf.QueryConfig;
import cc.carm.app.aliddns.model.RequestRegistry;
import cc.carm.app.aliddns.model.UpdateRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    private final SimpleDateFormat format;
    public final HashMap<String, UpdateRequest> requests;

    public RequestManager() {
        this.requests = new HashMap<>();
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    }

    public static boolean isIPV6Enabled() {
        String v6URL = QueryConfig.V6.get();
        return v6URL != null && v6URL.length() > 0;
    }

    public RequestRegistry getRegistry() {
        return AppConfig.REQUESTS.getNotNull();
    }

    public void doUpdate() {

        Main.info("[" + this.format.format(new Date()) + "]" + " 开始执行第" + getRegistry().getUpdateCount() + "次更新...");

        Main.info("从 " + QueryConfig.V4.getNotNull() + " 获取IPv4地址...");
        String IPv4 = getCurrentHostIP(false);
        Main.info("     获取完成，当前IPv4地址为 " + IPv4);

        String IPv6 = null;
        if (isIPV6Enabled() && getRegistry().hasV6Request()) {
            Main.info("从 " + QueryConfig.V6.getNotNull() + " 获取IPv6地址...");
            IPv6 = getCurrentHostIP(true);
            Main.info("     获取完成，当前IPv6地址为 " + IPv6);
        }

        Main.info("执行更新任务列表...");
        for (Map.Entry<String, UpdateRequest> entry : getRequests().entrySet()) {
            UpdateRequest currentRequest = entry.getValue();
            if (currentRequest.isIpv6() && IPv6 == null) {
                Main.info("记录 [" + entry.getKey() + "] 为IPv6任务，但本实例未启用IPv6，跳过。");
                continue;
            }
            try {
                currentRequest.doUpdate(currentRequest.isIpv6() ? IPv6 : IPv4);
            } catch (Exception exception) {
                Main.severe("在更新请求 [" + entry.getKey() + "] 时发生问题，请检查配置。");
                exception.printStackTrace();
            }
        }

        getRegistry().countUpdate();
    }

    public HashMap<String, UpdateRequest> getRequests() {
        return new HashMap<>(getRegistry().listRequests());
    }

    public static String getCurrentHostIP(boolean isIPV6) {
        StringBuilder result = new StringBuilder();
        String requestURL = isIPV6 ? QueryConfig.V6.getNotNull() : QueryConfig.V4.getNotNull();

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
            Main.severe("获取" + (isIPV6 ? "IPV6" : "IPV4") + "地址失败，请检查配置的请求连接和当前网络！");
            e.printStackTrace();
        }
        return result.toString();

    }

}
