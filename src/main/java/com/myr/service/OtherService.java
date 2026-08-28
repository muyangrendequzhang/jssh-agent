package com.myr.service;

import org.apache.sshd.client.session.ClientSession;
import org.springframework.stereotype.Service;

/**
 * 使用 Shell 通道，保持长连接保证内存等数据的一致性
 */
@Service
public class OtherService {

    private final ConnectService connectService;

    public OtherService(ConnectService connectService) {
        this.connectService = connectService;
    }

    public ClientSession getSession() {
        return connectService.getSession();
    }
}
