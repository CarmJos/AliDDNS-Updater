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

    public String getFullDomain() {
        if (getRecord().equals("@")) {
            return getDomain();
        } else {
            return getRecord() + "." + getDomain();
        }
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
                section.getBoolean("ipv6", false)
        );
    }

    /**
     * ??????????????????
     *
     * @param currentValue ??????????????????
     */
    public void doUpdate(String currentValue) throws ClientException {


        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setDomainName(getDomain());        // ?????????
        describeDomainRecordsRequest.setRRKeyWord(getRecord());  // ????????????
        describeDomainRecordsRequest.setType(getRecordType()); // ??????????????????

        // ??????????????????????????????????????????
        DescribeDomainRecordsResponse describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
        if (ConfigManager.isDebugMode()) {
            Main.debug(" \n" + JSON.toJSONString(describeDomainRecordsResponse, true));
        }
        // ???????????????????????????
        List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();

        if (domainRecords == null || domainRecords.size() == 0) {
            Main.error("    ?????????" + getDomain() + "?????????" + getRecordType() + "?????? ???" + getRecord() + "??? ?????????????????????????????????");
            return;
        }

        DescribeDomainRecordsResponse.Record record = domainRecords.get(0); //??????????????????

        String recordID = record.getRecordId(); //??????RecordID
        String recordValue = record.getValue();

        if (currentValue.length() > 0 && !currentValue.equals(recordValue)) {
            UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
            updateDomainRecordRequest.setRR(getRecord());
            updateDomainRecordRequest.setRecordId(recordID);
            updateDomainRecordRequest.setValue(currentValue);
            updateDomainRecordRequest.setType(getRecordType());

            //???????????????????????????
            UpdateDomainRecordResponse updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);

            if (ConfigManager.isDebugMode()) {
                Main.debug(" \n" + JSON.toJSONString(updateDomainRecordResponse, true));
            }

            if (recordID.equals(updateDomainRecordResponse.getRecordId())) {
                Main.info("     ?????? ???" + getFullDomain() + "??? ??????????????? " + currentValue + " ???");
            } else {
                Main.error("    ?????? ???" + getFullDomain() + "??? ????????????,???????????????????????????");
            }
        } else {
            Main.info("     ?????? ???" + getFullDomain() + "??? ????????????????????????");
        }
    }


}
