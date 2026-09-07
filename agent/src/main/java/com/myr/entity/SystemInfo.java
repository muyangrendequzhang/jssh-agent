package com.myr.entity;

import lombok.Data;

/**
 * 系统服务信息 DTO，字段与 jssh-back-end 的 com.myr.entity.SystemInfo 保持一致，
 * 用于解析后端 /system 接口返回的 systemd 服务状态列表。
 */
@Data
public class SystemInfo {

    /** 服务单元的名称，通常以 .service 结尾 */
    private String unit;

    /** 单元定义文件是否已被 systemd 成功加载 */
    private String load;

    /** 服务的高层级激活状态，反映服务是否正在运行 */
    private String active;

    /** 服务的低层级子状态 */
    private String sub;

    /** 对服务的简短描述 */
    private String description;
}