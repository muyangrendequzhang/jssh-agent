package com.myr.service;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import com.myr.utils.SshConnectUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 连接服务：承接 controller 的连接请求，复用 SshConnectUtils，
 * 并持有本次连接的 sshClient 与共享 session，供其它 service 复用。
 */
@Slf4j
@Service
public class ConnectService {

    private final SshConnectUtils sshConnectUtils = new SshConnectUtils();

    private SshClient sshClient;
    @Getter
    private ClientSession session;

    public Result<String> connect(ConnectParam param) {
        if (param == null || param.getHost() == null || param.getUser() == null) {
            return Result.fail("连接参数缺失：host / user 必填");
        }
        try {
            close();
            this.sshClient = sshConnectUtils.acceptAllSshClient();

            int port = param.getPort();
            if (param.getPassword() != null && !param.getPassword().isBlank()) {
                this.session = sshConnectUtils.usePasswordSession(
                        param.getUser(), param.getHost(), port, sshClient, param.getPassword());
            } else {
                this.session = sshConnectUtils.usePasswordKeySession(
                        param.getUser(), param.getHost(), port, sshClient, param.getPrivateKeyPath());
            }
            log.info("连接成功: {}@{}:{}", param.getUser(), param.getHost(), port);
            return Result.success("连接成功", param.getHost());
        } catch (Exception e) {
            log.error("连接失败: {}", param, e);
            close();
            return Result.fail("连接失败: " + e.getMessage());
        }
    }

    public synchronized void close() {
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                log.warn("关闭 session 失败: {}", e.getMessage());
            }
            session = null;
        }
        if (sshClient != null) {
            try {
                sshClient.stop();
                sshClient.close();
            } catch (IOException e) {
                log.warn("关闭 sshClient 失败: {}", e.getMessage());
            }
            sshClient = null;
        }
    }

}
