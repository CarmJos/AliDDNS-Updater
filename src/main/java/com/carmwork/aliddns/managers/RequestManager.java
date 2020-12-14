package com.carmwork.aliddns.managers;

import com.carmwork.aliddns.Main;
import com.carmwork.aliddns.enums.RecordType;
import com.carmwork.aliddns.model.UpdateRequest;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class RequestManager {

    public HashMap<String, UpdateRequest> requests;

    private String section = "UpdateRequests";

    public RequestManager() {
        this.requests = new HashMap<>();
    }

    public void loadRequests() {
        System.out.println("    Loading Requests.");
        requests.clear();

        FileConfiguration config = Main.getConfigManager().getConfig();

        for (String taskName : config.getConfigurationSection(section).getKeys(false)) {
            String accessKey = config.getString(section + "." + taskName + ".AccessKey", "");
            String accessKeySecret = config.getString(section + "." + taskName + ".AccessKeySecret", "");
            String domain = config.getString(section + "." + taskName + ".domain", "");
            String record = config.getString(section + "." + taskName + ".record", "");
            String type = config.getString(section + "." + taskName + ".type", "");
            UpdateRequest updateRequest = new UpdateRequest(accessKey, accessKeySecret, domain, record, type);
            if (updateRequest.getType() == RecordType.AAAA && !Main.isEnableV6()) {
                System.err.println("     Request @" + taskName + " using " + updateRequest.getType().name() + " ,but IPv6 is disabled, skipped.");
            } else {
                requests.put(taskName, updateRequest);
            }
        }
        System.out.println("    " + getRequests().size() + " request" + (getRequests().size() > 1 ? "s have" : " has") + " been loaded.");


    }

    public HashMap<String, UpdateRequest> getRequests() {
        return requests;
    }
}
