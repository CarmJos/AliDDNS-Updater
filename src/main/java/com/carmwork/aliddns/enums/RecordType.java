package com.carmwork.aliddns.enums;

public enum RecordType {
    AAAA(),
    A();

    RecordType() {
    }

    public static RecordType getRecordType(String s) {
        return s.equalsIgnoreCase("A") ? A : AAAA;
    }

}
