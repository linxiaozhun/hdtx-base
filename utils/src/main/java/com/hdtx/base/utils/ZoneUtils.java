package com.hdtx.base.utils;

import com.google.common.base.Strings;
import com.hdtx.base.constants.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class ZoneUtils {

    private static Logger logger = LoggerFactory.getLogger(ZoneUtils.class);

    public static final String BACK_UP_INSTANCE_IP_PREFIX = "10.1.";

    /**
     * 根据IP地址获取对应机房
     *
     * @param ip 需要查询的ip
     * @return 机房信息
     */
    public static ZoneType getZoneType(String ip) {
        if (ip.startsWith(BACK_UP_INSTANCE_IP_PREFIX)) {
            return ZoneType.DGLB;
        }
        return ZoneType.DGDC;
    }

    /**
     * 查询当前机器所在的机房
     */
    public static ZoneType currentInstanceZone() {
        String zoneType = System.getenv("TD_CUR_ZONE");
        if (!Strings.isNullOrEmpty(zoneType)) {
            return ZoneType.valueOf(zoneType);
        }
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
            while (nics.hasMoreElements()) {
                NetworkInterface nic = nics.nextElement();
                Enumeration<InetAddress> inetAddresses = nic.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String address = inetAddresses.nextElement().getHostAddress();
                    if (address.startsWith(BACK_UP_INSTANCE_IP_PREFIX)) {
                        return ZoneType.DGLB;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取IP地址失败", e);
        }
        return ZoneType.DGDC;
    }
}
