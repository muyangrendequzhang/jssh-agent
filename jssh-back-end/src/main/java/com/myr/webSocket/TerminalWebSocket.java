package com.myr.webSocket;

import com.myr.service.ConnectService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 终端 WebSocket：把前端 xterm 与后端共享的 SSH 会话（ChannelShell）打通。
 * &#064;ServerEndpoint  实例由容器创建，不能直接注入 Spring Bean，故用 ApplicationContextAware 取共享 session。
 */
@Slf4j
@Component
@ServerEndpoint("/podname")
public class TerminalWebSocket implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Session webSocketSession;
    private ClientSession clientSession;
    private ChannelShell shellChannel;
    private OutputStream channelIn;
    private InputStream channelOut;
    private Thread outputThread;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TerminalWebSocket.applicationContext = applicationContext;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.webSocketSession = session;
        try {
            // 复用 /connect 建立的共享会话
            //TODO 修改成token校验选取
            ConnectService connectService = applicationContext.getBean(ConnectService.class);
            this.clientSession = connectService.getSession();
            if (clientSession == null || !clientSession.isOpen()) {
                sendText("\r\n[系统] SSH 会话未建立，请先调用 /connect\r\n");
                return;
            }
            startShellChannel();
        } catch (Exception e) {
            log.error("初始化终端失败", e);
            sendText("\r\n[系统] 初始化终端失败: " + e.getMessage() + "\r\n");
        }
    }

    private void startShellChannel() throws Exception {
        // 持久 shell 通道：保持交互状态
        this.shellChannel = clientSession.createShellChannel();
        shellChannel.open().verify(10000);

        // getInvertedIn / getInvertedOut：标准输入流与标准输出流
        this.channelIn = shellChannel.getInvertedIn();
        this.channelOut = shellChannel.getInvertedOut();

        this.running.set(true);
        this.outputThread = new Thread(this::pumpOutput, "ssh-terminal-output");
        this.outputThread.setDaemon(true);
        this.outputThread.start();

        sendText("\r\n[系统] 连接成功！\r\n");
        log.info("WebSocket 终端已打开: {}", clientSession.getConnectAddress());
    }

    /**
     * 后台线程：读取 SSH 输出并推送到 WebSocket
     */
    private void pumpOutput() {
        byte[] buf = new byte[4096];
        int len;
        try {
            while (running.get() && (len = channelOut.read(buf)) != -1) {
                sendText(new String(buf, 0, len, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            if (running.get()) {
                log.warn("读取 SSH 输出失败: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.warn("推送终端输出异常: {}", e.getMessage());
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (channelIn == null) {
            return;
        }
        try {
            channelIn.write(message.getBytes(StandardCharsets.UTF_8));
            channelIn.flush();
        } catch (IOException e) {
            sendText("\r\n[系统] 发送命令失败: " + e.getMessage() + "\r\n");
        }
    }

    @OnClose
    public void onClose() {
        cleanup();
    }

    @OnError
    public void onError(Throwable error) {
        log.warn("WebSocket 错误: {}", error.getMessage());
        cleanup();
    }

    private synchronized void sendText(String text) {
        try {
            if (webSocketSession != null && webSocketSession.isOpen()) {
                webSocketSession.getBasicRemote().sendText(text);
            }
        } catch (IOException e) {
            log.warn("WebSocket 发送失败: {}", e.getMessage());
        }
    }

    private void cleanup() {
        running.set(false);
        if (outputThread != null) {
            outputThread.interrupt();
            outputThread = null;
        }
        if (shellChannel != null) {
            try {
                shellChannel.close();
            } catch (IOException e) {
                log.warn("关闭 shell 通道失败: {}", e.getMessage());
            }
            shellChannel = null;
        }
        if (channelIn != null) {
            try {
                channelIn.close();
            } catch (IOException ignored) {
            }
            channelIn = null;
        }
    }
}
