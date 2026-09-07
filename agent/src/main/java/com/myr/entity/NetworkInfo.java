package com.myr.entity;

import lombok.Data;

/**
 * 网络信息 DTO，字段与 jssh-back-end 的 com.myr.entity.NetworkInfo 保持一致，
 * 用于解析后端 /network 接口返回的网卡信息列表。
 */
@Data
public class NetworkInfo {

    /** 网卡名称，如 eth0、lo、wlan0 */
    private String interfaceName;

    /** 网卡状态标志，如 UP、DOWN、RUNNING */
    private String flags;

    /** MTU（最大传输单元），单位字节 */
    private Integer mtu;

    /** 网卡类型，如 Ethernet */
    private String interfaceType;

    /** 发送队列长度 */
    private Integer txQueueLength;

    /** MAC 地址（硬件地址） */
    private String macAddress;

    /** IPv4 地址 */
    private String ipv4Address;

    /** IPv4 子网掩码（点分十进制） */
    private String ipv4Netmask;

    /** IPv4 广播地址 */
    private String ipv4Broadcast;

    /** IPv6 地址（多个用逗号分隔） */
    private String ipv6Addresses;

    /** IPv6 前缀长度（多个用逗号分隔） */
    private String ipv6PrefixLengths;

    /** IPv6 作用域（多个用逗号分隔） */
    private String ipv6ScopeIds;

    /** 接收数据包总数 */
    private Long rxPackets;

    /** 接收总字节数 */
    private Long rxBytes;

    /** 接收错误包数 */
    private Long rxErrors;

    /** 接收丢包数 */
    private Long rxDropped;

    /** 接收溢出包数 */
    private Long rxOverruns;

    /** 接收帧错误数 */
    private Long rxFrameErrors;

    /** 发送数据包总数 */
    private Long txPackets;

    /** 发送总字节数 */
    private Long txBytes;

    /** 发送错误包数 */
    private Long txErrors;

    /** 发送丢包数 */
    private Long txDropped;

    /** 发送溢出包数 */
    private Long txOverruns;

    /** 发送载波丢失数 */
    private Long txCarrierLosses;

    /** 发送冲突数（仅以太网） */
    private Long txCollisions;
}