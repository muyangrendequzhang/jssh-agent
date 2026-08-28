package com.myr.service;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用 Shell 通道，保持长连接保证内存等数据的一致性
 */
@Service
public class OtherService {

    private static final String SEPARATOR = "@@@@";

    @Autowired
    private ConnectService connectService;

    public ClientSession getSession() {
        return connectService.getSession();
    }

    /**
     * 查询内存信息：在长连接 shell 上执行 cat /proc/meminfo 并解析为 MemoryInfo
     */
    public Result<MemoryInfo> getMemory() {
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            String output = queryShell(session, "cat /proc/meminfo; echo " + SEPARATOR + "; hostname");
            return Result.success(parseMemoryInfo(output));
        } catch (Exception e) {
            return Result.fail("查询内存失败: " + e.getMessage());
        }
    }

    private MemoryInfo parseMemoryInfo(String output) {
        String[] sections = output.split(SEPARATOR);
        String meminfo = sections.length > 0 ? sections[0] : "";
        String hostname = sections.length > 1 ? sections[1].trim().lines().findFirst().orElse("") : "";

        Map<String, Long> mem = new HashMap<>();
        for (String line : meminfo.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length < 2) {
                continue;
            }
            String key = parts[0].replace(":", "");
            try {
                mem.put(key, Long.parseLong(parts[1]));
            } catch (NumberFormatException ignored) {
                // 忽略非数值行
            }
        }

        long total = mem.getOrDefault("MemTotal", 0L);
        long free = mem.getOrDefault("MemFree", 0L);
        long available = mem.getOrDefault("MemAvailable", 0L);
        long swapTotal = mem.getOrDefault("SwapTotal", 0L);
        long swapFree = mem.getOrDefault("SwapFree", 0L);

        MemoryInfo info = new MemoryInfo();
        info.setTotalMemory(total);
        info.setFreeMemory(free);
        info.setAvailableMemory(available);
        info.setTotalSwap(swapTotal);
        info.setFreeSwap(swapFree);
        info.setBufferedMemory(mem.getOrDefault("Buffers", 0L));
        info.setCachedMemory(mem.getOrDefault("Cached", 0L));
        info.setPageTables(mem.getOrDefault("PageTables", 0L));
        info.setSlab(mem.getOrDefault("Slab", 0L));
        info.setKernelStack(mem.getOrDefault("KernelStack", 0L));

        long used = total - available;
        long usedSwap = swapTotal - swapFree;
        info.setUsedMemory(used);
        info.setUsagePercent(total > 0 ? round2(used * 100.0 / total) : 0.0);
        info.setUsedSwap(usedSwap);
        info.setSwapUsagePercent(swapTotal > 0 ? round2(usedSwap * 100.0 / swapTotal) : 0.0);

        info.setHostname(hostname);
        info.setTimestamp(System.currentTimeMillis());
        return info;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String queryShell(ClientSession session, String command) throws Exception {
        try (ChannelShell channel = session.createShellChannel()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setOut(out);
            channel.setErr(err);
            // shell 通道不会自动结束，追加 exit 使其执行完毕后关闭并返回输出
            channel.setIn(new ByteArrayInputStream((command + "\nexit\n").getBytes()));
            channel.open().verify(10000);
            channel.waitFor(Collections.singletonList(ClientChannelEvent.CLOSED), 10000);
            return out.toString();
        }
    }
}
