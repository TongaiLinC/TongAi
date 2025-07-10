package com.tawl.common.enums;

public enum NoticeStatus {
    /**
     * 未发布
     */
    NO_PUBLISH("未发布", "0"),

    /**
     * 已发布
     */
    PUBLISH("已发布", "1");

    /**
     * 类型
     */
    private String name;
    /**
     * 默认值
     */
    private String value;

    NoticeStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
