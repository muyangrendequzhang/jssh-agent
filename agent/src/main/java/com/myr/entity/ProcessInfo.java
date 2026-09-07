package com.myr.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 进程信息 DTO，字段与 jssh-back-end 的 com.myr.entity.ProcessInfo 保持一致，
 * 用于解析后端 /process 接口返回的进程列表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfo {

    /** 进程 ID */
    private Integer pid;

    /** 父进程 ID */
    private Integer parentPid;

    /** 进程所有者（用户名） */
    private String userName;

    /** 进程名称（可执行文件名） */
    private String name;

    /** 完整命令行（含参数） */
    private String commandLine;

    /** 进程启动时间 */
    private String startTime;

    /** CPU 使用率（百分比） */
    private Double cpuUsage;

    /** 内存使用率（百分比） */
    private Double memoryUsage;

    /** 虚拟内存大小（KB） */
    private Long virtualMemory;

    /** 物理内存大小（KB，常驻内存集） */
    private Long residentMemory;

    /** 进程状态 */
    private String status;

    /** 进程优先级 */
    private Integer priority;

    /** Nice 值（友好度） */
    private Integer niceValue;

    /** 关联的终端 */
    private String terminal;

    /** 进程累计占用 CPU 时间 */
    private String cpuTime;
}
