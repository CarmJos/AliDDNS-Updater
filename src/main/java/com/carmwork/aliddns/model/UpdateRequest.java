package com.carmwork.aliddns.model;

import com.carmwork.aliddns.enums.RecordType;

public class UpdateRequest {

    private String accessKey;
    private String accessKeySecret;
    private String domain;
    private String record;
    private RecordType type;

    public UpdateRequest(String accessKey, String accessKeySecret, String domain, String record, RecordType type) {
        this.accessKey = accessKey;
        this.accessKeySecret = accessKeySecret;
        this.domain = domain;
        this.record = record;
        this.type = type;
    }

    public UpdateRequest(String accessKey, String accessKeySecret, String domain, String record, String type) {
        this.accessKey = accessKey;
        this.accessKeySecret = accessKeySecret;
        this.domain = domain;
        this.record = record;
        this.type = RecordType.getRecordType(type);
    }

    public String getAccessKey() {
        return accessKey;
    }


    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getDomain() {
        return domain;
    }

    public String getRecord() {
        return record;
    }

    public RecordType getType() {
        return type;
    }
}
