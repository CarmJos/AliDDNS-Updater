package com.carmwork.aliddns.utils;

import com.carmwork.aliddns.Main;
import com.carmwork.aliddns.enums.RecordType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPAddressUtil {
    /**
     * 获取当前主机公网IP
     */
    public static String getCurrentHostIP(RecordType type) {
        String result = "";
        String requestURL = "";
        switch (type) {
            case AAAA: {
                requestURL = Main.getConfigManager().getConfig().getString("IPQueryURL.IPv6", "http://v6.ip.zxinc.org/getip");
                break;
            }
            case A: {
                requestURL = Main.getConfigManager().getConfig().getString("IPQueryURL.IPv4", "http://v4.ip.zxinc.org/getip");
                break;
            }
        }

        BufferedReader in = null;
        try {
            // 使用HttpURLConnection网络请求第三方接口
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);
            urlConnection.connect();

            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }

        return result;

    }

}
