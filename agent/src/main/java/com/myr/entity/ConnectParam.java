package com.myr.entity;

import lombok.Data;

/**
 * 连接参数 DTO，字段与 jssh-back-end 的 com.myr.entity.ConnectParam 保持一致，
 * 用于解析后端 /connection 接口返回的已保存连接列表。
 */
@Data
public class ConnectParam {

    /** 连接名称 */
    private String connectName;

    /** 主机地址 */
    private String host;

    /** SSH 端口，默认 22 */
    private int port = 22;

    /** 用户名 */
    private String user;

    /** 密码 */
    private String password;

    /** 私钥文件路径 */
    private String privateKeyPath;
}