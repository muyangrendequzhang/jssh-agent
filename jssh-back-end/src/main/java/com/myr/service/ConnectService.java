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

    public synchronized Result<String> connect(ConnectParam param) {
        if (param == null || param.getHost() == null || param.getUser() == null) {
            return Result.fail("连接参数缺失：host / user 必填");
        }
        if ((param.getPassword() == null || param.getPassword().isBlank())
                && (param.getPrivateKeyPath() == null || param.getPrivateKeyPath().isBlank())) {
            return Result.fail("请填写密码或上传私钥");
        }
        SshClient newClient = null;
        ClientSession newSession = null;
        try {
            newClient = sshConnectUtils.acceptAllSshClient();

            int port = param.getPort();
            if (param.getPassword() != null && !param.getPassword().isBlank()) {
                newSession = sshConnectUtils.usePasswordSession(
                        param.getUser(), param.getHost(), port, newClient, param.getPassword());
            } else {
                newSession = sshConnectUtils.usePasswordKeySession(
                        param.getUser(), param.getHost(), port, newClient, param.getPrivateKeyPath());
            }

            // 新连接成功后，再交换并释放旧连接，避免关闭正在建立的客户端
            SshClient oldClient = this.sshClient;
            ClientSession oldSession = this.session;
            this.sshClient = newClient;
            this.session = newSession;
            closeQuietly(oldSession, oldClient);

            log.info("连接成功: {}@{}:{}", param.getUser(), param.getHost(), port);
            return Result.success("连接成功", param.getHost());
        } catch (Exception e) {
            log.error("连接失败: {}", param, e);
            closeQuietly(newSession, newClient);
            return Result.fail("连接失败: " + e.getMessage());
        }
    }

    public synchronized void close() {
        closeQuietly(session, sshClient);
        session = null;
        sshClient = null;
    }

    private void closeQuietly(ClientSession s, SshClient c) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                log.warn("关闭 session 失败: {}", e.getMessage());
            }
        }
        if (c != null) {
            try {
                c.stop();
                c.close();
            } catch (IOException e) {
                log.warn("关闭 sshClient 失败: {}", e.getMessage());
            }
        }
    }

}
