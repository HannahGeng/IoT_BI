package com.yikekong.common;

public class SystemDefinition{
    //Indicator topic prefix in redis
    public static final String QUOTA_KEY_PREFIX = "ykk.quota.";
    //gps information key prefix in redis
    public static final String GPS_KEY_PREFIX = "ykk.gps.quota.";
    //Client state key prefix in redis
    public static final String CLIENT_INFO = "ykk.client.";

    //Client state key prefix in redis
    public static final String CYCLE_KEY = "ykk.cycle.";

    //Device storage key prefix in redis
    public static final  String DEVICE_KEY = "ykk.device";
    //Device index name in ES
    public static final String ES_INDEX_NAME = "Device";
    //Device Id field prefix stored by topic in redis
    public static final String QUOTA_SUBJECT_DEVICE_KEY_PREFIX = "ykk.quota.deviceId.";

    /**
     * Prefix of device indicator storage key in redis
     */
    public static final String DEVICE_QUOTA_KEY_PREFIX = "ykk.device.quotaList.";
}
