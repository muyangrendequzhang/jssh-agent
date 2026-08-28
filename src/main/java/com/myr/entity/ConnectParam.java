package com.myr.entity;

import lombok.Data;

/**
 * 连接参数
 */
@Data
public class ConnectParam {

    private String host;
    private int port = 22;
    private String user;
    private String password;
    private String privateKeyPath;
}
