package com.hdtx.base.utils;

import com.hdtx.base.constants.ZoneType;

/**
 * @Author liubin
 * @Date 2017/5/15 10:47
 */
public class ZkUtils {


    public static final String ZK_ROOT = "/zkjava";

    private static String baseZkSchedulePath(String applicationName, String profile) {
        return String.format("%s/%s/%s", ZK_ROOT, applicationName, profile);
    }

    public static String createZkSchedulerLeaderPath(String applicationName, String profile) {
        return String.format("%s/schedulers", baseZkSchedulePath(applicationName, profile));
    }

    public static String createZkSchedulerZoneLeaderPath(String applicationName, String profile, ZoneType zone) {
        return String.format("%s/%s/schedulers", baseZkSchedulePath(applicationName, profile), zone);
    }

    public static String createZkXxlJobLockPath(String applicationName, String profile, String jobName) {
        return String.format("%s/%s/%s/xxljob/%s", ZK_ROOT, applicationName, profile, jobName);
    }

    public static String createZkLeaderRecordPath(String applicationName, String profile) {
        return String.format("%s/leader_record", baseZkSchedulePath(applicationName, profile));
    }

    public static String createZkLeaderRecordPath(String applicationName, String profile, ZoneType zoneType) {
        return String.format("%s/%s/leader_record", baseZkSchedulePath(applicationName, profile), zoneType);
    }

    public static String createJobExpression(String applicationName, String profile, String jobName) {
        return String.format("%s/cron_expression/%s", baseZkSchedulePath(applicationName, profile),
                jobName);
    }

    public static String createJobExpression(String applicationName, String profile, String jobName,
            ZoneType zoneType) {
        return String.format("%s/%s/cron_expression/%s", baseZkSchedulePath(applicationName, profile), zoneType,
                jobName);
    }

    public static String createJobStatus(String applicationName, String profile, String jobName) {
        return String.format("%s/status/%s", baseZkSchedulePath(applicationName, profile), jobName);
    }

    public static String createZoneJobStatus(String applicationName, String profile, String jobName, ZoneType zone) {
        return String.format("%s/%s/status/%s", baseZkSchedulePath(applicationName, profile), zone, jobName);
    }

    public static String createOperationRecordPath(String applicationName, String profile, String jobName) {
        return String.format("%s/operation_record/%s", baseZkSchedulePath(applicationName, profile), jobName);
    }
}
