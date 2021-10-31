package cc.carm.app.aliddns.model;

import cc.carm.app.aliddns.Main;
import cc.carm.app.aliddns.manager.ConfigManager;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UpdateRequest {


    @NotNull
    private final String accessKey;
    @NotNull
    private final String accessKeySecret;

    private final DefaultProfile profile;
    private final IAcsClient client;


    private final String domain;
    private final String record;

    private final boolean ipv6;

    private String recordID;

    public UpdateRequest(@NotNull String accessKey, @NotNull String accessKeySecret, String domain, String record, boolean ipv6) {
        this.accessKey = accessKey;
        this.accessKeySecret = accessKeySecret;
        this.domain = domain;
        this.record = record;
        this.ipv6 = ipv6;

        this.profile = DefaultProfile.getProfile(ConfigManager.getRegionID(), getAccessKey(), getAccessKeySecret());
        this.client = new DefaultAcsClient(profile);

    }

    @NotNull
    public String getAccessKey() {
        return accessKey;
    }

    @NotNull
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getDomain() {
        return domain;
    }

    public String getRecord() {
        return record;
    }

    public boolean isIpv6() {
        return ipv6;
    }

    public String getRecordType() {
        return isIpv6() ? "AAAA" : "A";
    }

    @NotNull
    public static UpdateRequest readConfiguration(@NotNull ConfigurationSection section) {
        return new UpdateRequest(
                section.getString("AccessKey", "xx"),
                section.getString("AccessSecret", "xx"),
                section.getString("domain", "xx"),
                section.getString("record", "xx"),
                section.getBoolean("AccessSecret", false)
        );
    }


    /**
     * 检查是否需要进行更新操作
     *
     * @param currentValue 当前数值内容
     * @return 若当前数值与最新数值不同，则返回true。
     */
    public boolean shouldUpdate(String currentValue) throws ClientException {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setDomainName(getDomain());        // 主域名
        describeDomainRecordsRequest.setRRKeyWord(getRecord());  // 主机记录
        describeDomainRecordsRequest.setType(getRecordType()); // 解析记录类型

        // 获取主域名的所有解析记录列表
        DescribeDomainRecordsResponse describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
        if (ConfigManager.isDebugMode()) {
            Main.debug(" ", JSON.toJSONString(describeDomainRecordsResponse, true));
        }
        // 最新的一条解析记录
        List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();

        if (domainRecords == null || domainRecords.size() == 0) {
            Main.error("    域名“" + getDomain() + "”下无" + getRecordType() + "记录 “" + getRecord() + "” ，请检查阿里云控制台。");
            return false;
        }

        DescribeDomainRecordsResponse.Record record = domainRecords.get(0); //得到最新一条

        this.recordID = record.getRecordId(); //记录RecordID
        String recordValue = record.getValue();

        return currentValue.length() > 0 && !currentValue.equals(recordValue);
    }

    /**
     * 进行更新操作
     *
     * @param currentValue 当前数值内容
     */
    public boolean doUpdate(String currentValue) throws ClientException {
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
        updateDomainRecordRequest.setRR(getRecord());
        updateDomainRecordRequest.setRecordId(recordID);
        updateDomainRecordRequest.setValue(currentValue);
        updateDomainRecordRequest.setType(getRecordType());

        //发出请求，收到回复
        UpdateDomainRecordResponse updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);

        if (ConfigManager.isDebugMode()) {
            Main.debug(" ", JSON.toJSONString(updateDomainRecordResponse, true));
        }

        return this.recordID.equals(updateDomainRecordResponse.getRecordId());
    }


}
