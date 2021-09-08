package com.hdtx.base.constants;

public enum ZkLeaderScheduleModel {
    /**
     * 默认选项
     * 标准模式,一个zk集群中只有一个leader(不管存在多少个机房)
     */
    STANDARD,
    /**
     * 聚焦模式,在多个机房中指定一个机房作为焦点。leader将从该机房的实例中选举
     */
    FOCUS,
    /**
     * 每个机房都有一个leader
     */
    EACH
}
