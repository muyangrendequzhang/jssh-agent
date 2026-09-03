package com.myr.service.serviceImpl;

import com.myr.entity.Result;
import com.myr.entity.SystemInfo;
import com.myr.service.ConnectService;
import com.myr.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private ConnectService connectService;

    private ClientSession getSession() {
        return connectService.getSession();
    }

    @Override
    public Result<List<SystemInfo>> getSystemInfo() {
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            String output = exec(session,
                    "systemctl list-units --type=service --all --no-legend --no-pager --plain");
            log.info("systemctl 原始输出长度={}, 前300字符={}",
                    output == null ? 0 : output.length(), preview(output));
            List<SystemInfo> list = parse(output);
            log.info("解析到服务数={}", list.size());
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("查询服务信息失败: " + e.getMessage());
        }
    }

    /**
     * 解析 systemctl list-units 输出：UNIT LOAD ACTIVE SUB DESCRIPTION（描述含空格，为其余列拼接）
     */
    private List<SystemInfo> parse(String output) {
        List<SystemInfo> list = new ArrayList<>();
        if (output == null || output.isBlank()) {
            return list;
        }
        for (String line : output.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            // systemctl 行固定前 4 列（UNIT/LOAD/ACTIVE/SUB），描述可能为空
            if (parts.length < 4) {
                continue;
            }
            SystemInfo info = new SystemInfo();
            info.setUnit(parts[0]);
            info.setLoad(parts[1]);
            info.setActive(parts[2]);
            info.setSub(parts[3]);
            info.setDescription(parts.length > 4
                    ? String.join(" ", Arrays.copyOfRange(parts, 4, parts.length))
                    : "");
            list.add(info);
        }
        log.debug("解析结果: {}", list);
        return list;
    }

    private String preview(String s) {
        if (s == null) {
            return "null";
        }
        String v = s.trim();
        return v.substring(0, Math.min(v.length(), 300)).replace("\n", "\\n");
    }

    private String exec(ClientSession session, String command) throws Exception {
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
