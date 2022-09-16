package cc.carm.app.aliddns.model;

import cc.carm.app.aliddns.Main;
import cc.carm.app.aliddns.conf.ServiceConfig;
import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UpdateRequest {


    @NotNull
    private final String accessKey;
    @NotNull
    private final String accessKeySecret;

    private final String domain;
    private final String record;

    private final boolean ipv6;

    public UpdateRequest(@NotNull String accessKey, @NotNull String accessKeySecret, String domain, String record, boolean ipv6) {
        this.accessKey = accessKey;
        this.accessKeySecret = accessKeySecret;
        this.domain = domain;
        this.record = record;
        this.ipv6 = ipv6;
    }


    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("domain", getDomain());
        data.put("record", getRecord());
        data.put("ipv6", isIpv6());

        data.put("access-key", getAccessKey());
        data.put("access-secret", getAccessKeySecret());
        return data;
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

    /**
     * 进行更新操作
     *
     * @param currentValue 当前数值内容
     */
    public void doUpdate(String currentValue) throws ClientException {

        DefaultProfile profile = DefaultProfile.getProfile(ServiceConfig.REGION_ID.getNotNull(), getAccessKey(), getAccessKeySecret());
        DefaultAcsClient client = new DefaultAcsClient(profile);

        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        describeDomainRecordsRequest.setDomainName(getDomain());        // 主域名
        describeDomainRecordsRequest.setRRKeyWord(getRecord());  // 主机记录
        describeDomainRecordsRequest.setType(getRecordType()); // 解析记录类型

        // 获取主域名的所有解析记录列表
        DescribeDomainRecordsResponse describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
        Main.debug(" \n" + JSON.toJSONString(describeDomainRecordsResponse, true));

        // 最新的一条解析记录
        List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();

        if (domainRecords == null || domainRecords.size() == 0) {
            Main.severe("    域名“" + getDomain() + "”下无" + getRecordType() + "记录 “" + getRecord() + "” ，请检查阿里云控制台。");
            return;
        }

        DescribeDomainRecordsResponse.Record record = domainRecords.get(0); //得到最新一条

        String recordID = record.getRecordId(); //记录RecordID
        String recordValue = record.getValue();

        if (currentValue.length() > 0 && !currentValue.equals(recordValue)) {
            UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
            updateDomainRecordRequest.setRR(getRecord());
            updateDomainRecordRequest.setRecordId(recordID);
            updateDomainRecordRequest.setValue(currentValue);
            updateDomainRecordRequest.setType(getRecordType());

            //发出请求，收到回复
            UpdateDomainRecordResponse updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);
            Main.debug(" \n" + JSON.toJSONString(updateDomainRecordResponse, true));

            if (recordID.equals(updateDomainRecordResponse.getRecordId())) {
                Main.info("     记录 “" + getFullDomain() + "” 成功更新为 " + currentValue + " 。");
            } else {
                Main.severe("    记录 “" + getFullDomain() + "” 更新失败,请检查网络与配置。");
            }
        } else {
            Main.info("     记录 “" + getFullDomain() + "” 无需更新，跳过。");
        }
    }


}
