package com.myr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkInfo {
    // ==================== 网卡基本信息 ====================

    /**
     * 网卡名称
     * 例如：eth0（以太网）、lo（回环接口）、wlan0（无线网卡）
     */
    private String interfaceName;

    /**
     * 网卡状态标志
     * 例如：UP（已启用）、DOWN（已停用）、RUNNING（正在运行）
     * 完整标志示例：<UP,BROADCAST,RUNNING,MULTICAST>
     */
    private String flags;

    /**
     * MTU（最大传输单元）
     * 单位：字节，以太网默认 1500，回环接口通常 65536
     */
    private Integer mtu;

    /**
     * 网卡类型
     * 例如：Ethernet（以太网）
     */
    private String interfaceType;

    /**
     * 发送队列长度
     * 例如：txqueuelen 1000
     */
    private Integer txQueueLength;

    // ==================== MAC 地址 ====================

    /**
     * MAC 地址（硬件地址）
     * 格式：00:11:22:33:44:55
     * 仅物理网卡有该字段，回环接口无
     */
    private String macAddress;

    // ==================== IPv4 地址信息 ====================

    /**
     * IPv4 地址
     * 例如：192.168.1.100
     */
    private String ipv4Address;

    /**
     * IPv4 子网掩码（点分十进制格式）
     * 例如：255.255.255.0
     */
    private String ipv4Netmask;

    /**
     * IPv4 广播地址
     * 例如：192.168.1.255
     */
    private String ipv4Broadcast;

    // ==================== IPv6 地址信息 ====================

    /**
     * IPv6 地址（多个地址用逗号分隔）
     * 例如：fe80::211:22ff:fe33:4455, 240e:390:1234:5678::1
     */
    private String ipv6Addresses;

    /**
     * IPv6 前缀长度（多个用逗号分隔，与 ipv6Addresses 对应）
     * 例如：64, 64
     */
    private String ipv6PrefixLengths;

    /**
     * IPv6 作用域（多个用逗号分隔，与 ipv6Addresses 对应）
     * 例如：link, global
     */
    private String ipv6ScopeIds;

    // ==================== 接收数据包统计（RX） ====================

    /**
     * 接收数据包总数
     * 例如：123456
     */
    private Long rxPackets;

    /**
     * 接收总字节数
     * 例如：98765432
     */
    private Long rxBytes;

    /**
     * 接收错误包数
     * 例如：0
     */
    private Long rxErrors;

    /**
     * 接收丢包数
     * 例如：0
     */
    private Long rxDropped;

    /**
     * 接收溢出包数
     * 例如：0
     */
    private Long rxOverruns;

    /**
     * 接收帧错误数
     * 例如：0
     */
    private Long rxFrameErrors;

    // ==================== 发送数据包统计（TX） ====================

    /**
     * 发送数据包总数
     * 例如：65432
     */
    private Long txPackets;

    /**
     * 发送总字节数
     * 例如：12345678
     */
    private Long txBytes;

    /**
     * 发送错误包数
     * 例如：0
     */
    private Long txErrors;

    /**
     * 发送丢包数
     * 例如：0
     */
    private Long txDropped;

    /**
     * 发送溢出包数
     * 例如：0
     */
    private Long txOverruns;

    /**
     * 发送载波丢失数
     * 例如：0
     */
    private Long txCarrierLosses;

    /**
     * 发送冲突数（仅以太网）
     * 例如：0
     */
    private Long txCollisions;

}
