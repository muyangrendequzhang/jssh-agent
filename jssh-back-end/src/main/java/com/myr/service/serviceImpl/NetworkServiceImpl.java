package com.myr.service.serviceImpl;

import com.myr.entity.NetworkInfo;
import com.myr.entity.Result;
import com.myr.service.ConnectService;
import com.myr.service.NetworkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class NetworkServiceImpl implements NetworkService {

    private static final Pattern HEADER = Pattern.compile("^\\d+:\\s+(\\S+):\\s+<([^>]+)>\\s+mtu\\s+(\\d+).*?(?:qlen\\s+(\\d+))?.*");
    private static final Pattern LINK = Pattern.compile("^link/(\\S+)\\s+(\\S+).*");
    private static final Pattern INET4 = Pattern.compile("^inet\\s+(\\S+)/(\\d+)(?:\\s+brd\\s+(\\S+))?.*");
    private static final Pattern INET6 = Pattern.compile("^inet6\\s+(\\S+)/(\\d+)\\s+scope\\s+(\\S+).*");

    @Autowired
    private ConnectService connectService;

    private ClientSession getSession() {
        return connectService.getSession();
    }

    @Override
    public Result<List<NetworkInfo>> getNetworkInfo() {
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            String ipOutput = exec(session, "ip addr show");
            String devOutput = exec(session, "cat /proc/net/dev");

            Map<String, NetworkInfo> map = parseInterfaces(ipOutput);
            applyCounters(map, devOutput);

            List<NetworkInfo> list = new ArrayList<>(map.values());
            log.info("解析到网卡数={}", list.size());
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("查询网络信息失败: " + e.getMessage());
        }
    }

    private Map<String, NetworkInfo> parseInterfaces(String output) {
        Map<String, NetworkInfo> map = new LinkedHashMap<>();
        if (output == null) {
            return map;
        }
        NetworkInfo current = null;
        for (String raw : output.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }
            Matcher m = HEADER.matcher(line);
            if (m.matches()) {
                current = new NetworkInfo();
                String name = m.group(1);
                current.setInterfaceName(name);
                current.setFlags(m.group(2));
                current.setMtu(parseInt(m.group(3)));
                current.setTxQueueLength(parseInt(m.group(4)));
                map.put(name, current);
                continue;
            }
            if (current == null) {
                continue;
            }
            Matcher lm = LINK.matcher(line);
            if (lm.matches()) {
                String linkType = lm.group(1);
                current.setInterfaceType("loopback".equals(linkType) ? "Loopback" : "Ethernet");
                if (!"loopback".equals(linkType)) {
                    current.setMacAddress(lm.group(2));
                }
                continue;
            }
            Matcher v4 = INET4.matcher(line);
            if (v4.matches()) {
                // ipv4（取第一个）
                if (current.getIpv4Address() == null) {
                    current.setIpv4Address(v4.group(1));
                    current.setIpv4Netmask(prefixToNetmask(parseInt(v4.group(2))!=null?parseInt(v4.group(2)):-1));
                    current.setIpv4Broadcast(v4.group(3));
                }
                continue;
            }
            Matcher v6 = INET6.matcher(line);
            if (v6.matches()) {
                current.setIpv6Addresses(append(current.getIpv6Addresses(), v6.group(1)));
                current.setIpv6PrefixLengths(append(current.getIpv6PrefixLengths(), v6.group(2)));
                current.setIpv6ScopeIds(append(current.getIpv6ScopeIds(), v6.group(3)));
            }
        }
        return map;
    }

    private void applyCounters(Map<String, NetworkInfo> map, String devOutput) {
        if (devOutput == null) {
            return;
        }
        for (String raw : devOutput.split("\\r?\\n")) {
            String line = raw.trim();
            if (!line.contains(":")) {
                continue; // 跳过表头
            }
            int idx = line.indexOf(':');
            String name = line.substring(0, idx).trim();
            NetworkInfo info = map.get(name);
            if (info == null) {
                continue;
            }
            String[] p = line.substring(idx + 1).trim().split("\\s+");
            if (p.length < 16) {
                continue;
            }
            info.setRxBytes(parseLong(p[0]));
            info.setRxPackets(parseLong(p[1]));
            info.setRxErrors(parseLong(p[2]));
            info.setRxDropped(parseLong(p[3]));
            info.setRxOverruns(parseLong(p[4]));    // fifo
            info.setRxFrameErrors(parseLong(p[5])); // frame
            info.setTxBytes(parseLong(p[8]));
            info.setTxPackets(parseLong(p[9]));
            info.setTxErrors(parseLong(p[10]));
            info.setTxDropped(parseLong(p[11]));
            info.setTxOverruns(parseLong(p[12]));     // fifo
            info.setTxCollisions(parseLong(p[13]));   // colls
            info.setTxCarrierLosses(parseLong(p[14])); // carrier
        }
    }

    private String append(String current, String value) {
        if (current == null || current.isBlank()) {
            return value;
        }
        return current + "," + value;
    }

    private String prefixToNetmask(int prefix) {
        if (prefix < 0 || prefix > 32) {
            return "0.0.0.0";
        }
        long mask = prefix == 0 ? 0L : (0xFFFFFFFFL << (32 - prefix)) & 0xFFFFFFFFL;
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xFF, (mask >> 16) & 0xFF, (mask >> 8) & 0xFF, mask & 0xFF);
    }

    private Integer parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Long parseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
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
