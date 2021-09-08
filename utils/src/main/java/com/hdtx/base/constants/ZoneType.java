package com.hdtx.base.constants;

/**
 * 实例所属机房
 * Created by huangwenchang
 */
public enum ZoneType{
    DGDC("DGDC", "东莞东城"),
    GLOBAL("GLOBAL", "全局"),
    DGLB("DGLB", "东莞寮步");

    String value;
    String desc;

    ZoneType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getDescByValue(String value) {
        for (ZoneType zoneType : ZoneType.values()) {
            if (value.equals(zoneType.getValue())) {
                return zoneType.getDesc();
            }
        }
        return "未知";
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
