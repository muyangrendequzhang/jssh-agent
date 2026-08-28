package com.myr.service;

import com.myr.entity.Result;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * 使用 exec 通道，用于 cmd 交互
 */
@Service
public class CmdService {

    private final ConnectService connectService;

    public CmdService(ConnectService connectService) {
        this.connectService = connectService;
    }

    public Result<String> cmdExec(String command) {
        ClientSession session = connectService.getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            String output = exec(session, command);
            return Result.success(output);
        } catch (IOException e) {
            return Result.fail("执行失败: " + e.getMessage());
        }
    }

    private String exec(ClientSession session, String command) throws IOException {
        try (ChannelExec channel = session.createExecChannel(command)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setOut(out);
            channel.setErr(err);
            channel.open().verify(10000);
            channel.waitFor(Collections.singletonList(ClientChannelEvent.CLOSED), 10000);
            return out.toString();
        }
    }
}
