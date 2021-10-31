package com.carmwork.aliddns.managers;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.carmwork.aliddns.Main;
import com.carmwork.aliddns.enums.RecordType;
import com.carmwork.aliddns.model.UpdateRequest;
import com.carmwork.aliddns.utils.JsonFormatTool;
import com.google.gson.Gson;

import java.util.List;

/**
 * 动态域名解析
 */
public class DNSManager {

    public static void updateDNS(String currentHost, UpdateRequest request) {

        try {
            /*
             * 地域ID参考https://help.aliyun.com/knowledge_detail/40654.html?spm=5176.13910061.0.0.5af422c8KhBIfU&aly_as=hV5o5h29N
             */
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", // 地域ID
                    request.getAccessKey(), request.getAccessKeySecret());

            IAcsClient client = new DefaultAcsClient(profile);

            checkAndUpdateIp(currentHost, client, request.getDomain(), request.getRecord(), request.getType());

        } catch (Exception e) {
            // java.net.UnknownHostException: alidns.aliyuncs.com
            e.printStackTrace();
        }

    }

    /**
     * @param client
     * @param domainName  您的域名，如 baidu.com
     * @param ipRRKeyWord 您的主机记录，如 www
     * @param type        ipv4 填 A ，ipv6 填 AAAA
     */
    private static void checkAndUpdateIp(String currentHost, IAcsClient client, String domainName, String ipRRKeyWord, RecordType type) {

        Gson gson = new Gson();
        // 查询指定二级域名的最新解析记录
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        // 主域名
        describeDomainRecordsRequest.setDomainName(domainName);
        // 主机记录
        describeDomainRecordsRequest.setRRKeyWord(ipRRKeyWord);
        // 解析记录类型
        describeDomainRecordsRequest.setType(type.name());

        // 获取主域名的所有解析记录列表
        DescribeDomainRecordsResponse describeDomainRecordsResponse = null;
        // 最新的一条解析记录
        List<DescribeDomainRecordsResponse.Record> domainRecords = null;
        try {

            describeDomainRecordsResponse = client.getAcsResponse(describeDomainRecordsRequest);
            if (Main.isOutputDomainRecordsResponse()) {
                System.out.println("     " + JsonFormatTool.formatJson(gson.toJson(describeDomainRecordsResponse), "     "));
            }

            domainRecords = describeDomainRecordsResponse.getDomainRecords();

        } catch (ClientException e1) {
            if (Main.isOutputDomainRecordsResponse()) {
                System.out.println("     " + e1.getMessage());
            }

        }

        if (domainRecords == null) {
            System.err.println("     Failed to get domain records.");
            System.err.println("     Please check your configuration.");
            return;
        }

        if (domainRecords.size() != 0) {
            DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
            // 记录ID
            String recordId = record.getRecordId();
            // 记录值
            String recordsValue = record.getValue();

            if (currentHost.length() > 0 && !currentHost.equals(recordsValue)) {
                System.out.println("     Updating...");
                // 修改解析记录
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                // 主机记录
                updateDomainRecordRequest.setRR(ipRRKeyWord);
                // 记录ID
                updateDomainRecordRequest.setRecordId(recordId);
                // 将主机记录值改为当前主机IP
                updateDomainRecordRequest.setValue(currentHost);
                // 解析记录类型
                updateDomainRecordRequest.setType(type.name());
                // 修改解析记录
                UpdateDomainRecordResponse updateDomainRecordResponse = null;
                try {
                    updateDomainRecordResponse = client.getAcsResponse(updateDomainRecordRequest);
                    if (Main.isOutputDomainRecordsResponse()) {
                        System.out.println("     " + JsonFormatTool.formatJson(gson.toJson(updateDomainRecordResponse), "     "));
                    }
                    if (recordId.equals(updateDomainRecordResponse.getRecordId())) {
                        System.out.println("     Update Success! ");
                        System.out.println("     " + ipRRKeyWord + "." + domainName + "->" + currentHost);
                        System.out.println();
                    } else {
                        System.err.println("     Update failed！");
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("     Update skipped.");
            }
        } else {
            System.err.println("     Update failed. Domain record doesn't exist.");
        }
    }
}
