package com.myr.entity;

import lombok.Data;

/**
 * 内存信息 DTO，字段与 jssh-back-end 的 com.myr.entity.MemoryInfo 保持一致，
 * 用于解析后端 /memory 接口返回的内存信息。
 */
@Data
public class MemoryInfo {

    /** 总物理内存 (MemTotal) */
    private long totalMemory;

    /** 总交换分区大小 (SwapTotal) */
    private long totalSwap;

    /** 空闲物理内存 (MemFree) */
    private long freeMemory;

    /** 空闲交换分区 (SwapFree) */
    private long freeSwap;

    /** 可用内存 (MemAvailable)，包含可回收缓存 */
    private long availableMemory;

    /** 缓冲 (Buffers) */
    private long bufferedMemory;

    /** 缓存 (Cached) */
    private long cachedMemory;

    /** 页表占用 (PageTables) */
    private long pageTables;

    /** 内核 slab 缓存 (Slab) */
    private long slab;

    /** 内核栈 (KernelStack) */
    private long kernelStack;

    /** 已用内存 = total - available */
    private long usedMemory;

    /** 内存使用率百分比 (used/total * 100) */
    private double usagePercent;

    /** 已用交换分区 = total - free */
    private long usedSwap;

    /** 交换分区使用率 */
    private double swapUsagePercent;

    /** 主机名 */
    private String hostname;

    /** 采集时间戳 (毫秒) */
    private long timestamp;
}