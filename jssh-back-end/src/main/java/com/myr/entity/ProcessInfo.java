package com.myr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfo {

    /**
     * 进程 ID（PID）
     * ps -ef 第一列之外，实际是第二列
     */
    private Integer pid;

    /**
     * 父进程 ID（PPID）
     * 用于构建进程树
     */
    private Integer parentPid;

    /**
     * 进程所有者（用户名）
     * UID 列对应的用户名
     */
    private String userName;

    // ============ 进程身份信息 ============

    /**
     * 进程名称
     * CMD 列的第一个字段（可执行文件名）
     */
    private String name;

    /**
     * 完整命令行
     * CMD 列的完整内容（含参数）
     */
    private String commandLine;

    /**
     * 进程启动时间
     * STIME 列
     */
    private String startTime;

    // ============ 资源使用信息（ps -ef 不直接提供，但可通过其他方式获取） ============

    /**
     * CPU 使用率（百分比）
     * 来自 ps aux 的 %CPU 列，或 top 命令
     */
    private Double cpuUsage;

    /**
     * 内存使用率（百分比）
     * 来自 ps aux 的 %MEM 列，或 top 命令
     */
    private Double memoryUsage;

    /**
     * 虚拟内存大小（KB）
     * 来自 ps aux 的 VSZ 列
     */
    private Long virtualMemory;

    /**
     * 物理内存大小（KB，常驻内存集）
     * 来自 ps aux 的 RSS 列
     */
    private Long residentMemory;

    // ============ 进程状态信息 ============

    /**
     * 进程状态
     * 来自 ps aux 的 STAT 列
     * R=运行中, S=睡眠, D=不可中断, Z=僵尸, T=暂停
     */
    private String status;

    /**
     * 进程优先级
     * 来自 ps -eo pri,ni
     */
    private Integer priority;

    /**
     * Nice 值（友好度）
     * 范围 -20 到 19，越低优先级越高
     */
    private Integer niceValue;


    /**
     * 关联的终端
     * TTY 列，? 表示守护进程
     */
    private String terminal;

    /**
     * 进程累计占用 CPU 时间
     * TIME 列，格式：HH:MM:SS
     */
    private String cpuTime;
}
