package com.myr.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MemoryInfo implements Serializable {

    // ========== 总量统计 ==========
    private long totalMemory;        // 总物理内存 (MemTotal)
    private long totalSwap;          // 总交换分区大小 (SwapTotal)

    // ========== 使用/空闲分布 ==========
    private long freeMemory;         // 空闲物理内存 (MemFree)
    private long freeSwap;           // 空闲交换分区 (SwapFree)
    private long availableMemory;    // 可用内存 (MemAvailable)，包含可回收缓存

    // ========== 内核与缓存细节 ==========
    private long bufferedMemory;     // 缓冲 (Buffers)
    private long cachedMemory;       // 缓存 (Cached)
    private long pageTables;         // 页表占用 (PageTables)
    private long slab;              // 内核slab缓存 (Slab)
    private long kernelStack;       // 内核栈 (KernelStack)

    // ========== 衍生计算字段 ==========
    private long usedMemory;        // 已用内存 = total - available (或 free + buff/cache)
    private double usagePercent;    // 使用率百分比 (used/total * 100)
    private long usedSwap;          // 已用交换分区 = total - free
    private double swapUsagePercent; // 交换分区使用率

    // ========== 附加系统信息 ==========
    private String hostname;         // 主机名
    private long timestamp;          // 采集时间戳 (毫秒)
}
