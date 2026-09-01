package com.myr.service.serviceImpl;

import com.myr.entity.ProcessInfo;
import com.myr.entity.Result;
import com.myr.service.ConnectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class ProcessService {

    @Autowired
    private ConnectService connectService;

    public ClientSession getSession() {
        return connectService.getSession();
    }

    public Result<List<ProcessInfo>> getProcessInfo() {
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            String output = exec(session,
                    "ps -eo pid,ppid,user,%cpu,%mem,vsz,rss,stat,pri,ni,start,time,tty,args"
                            + " | awk 'NR>1{"
                            + "off=($11 ~ /^(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)$/) ? 1 : 0; "
                            + "st= off ? $11\" \"$12 : $11; "
                            + "tm= $(12+off); ty= $(13+off); "
                            + "cmd= $(14+off); for(i=15+off;i<=NF;i++) cmd=cmd \" \" $i; "
                            + "printf \"%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\t%s\\n\", "
                            + "$1,$2,$3,$4,$5,$6,$7,$8,$9,$10,st,tm,ty,cmd}'");
            List<ProcessInfo> list = parseProcessInfo(output);
            // 按 内存 使用率降序，其次按 CPU 使用率降序（null 排最后）
            list.sort(
                    Comparator.comparing(ProcessInfo::getMemoryUsage,
                                    Comparator.nullsLast(Comparator.reverseOrder()))
                            .thenComparing(ProcessInfo::getCpuUsage,
                                    Comparator.nullsLast(Comparator.reverseOrder())));
            log.info("进程原始输出长度={}, 前200字符={}", output == null ? 0 : output.length(), shortPreview(output));
            log.info("解析到进程数={}", list.size());
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("查询进程信息失败: " + e.getMessage());
        }
    }

    private List<ProcessInfo> parseProcessInfo(String output) {
        List<ProcessInfo> list = new ArrayList<>();
        if (output == null || output.isBlank()) {
            return list;
        }
        for (String line : output.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            ProcessInfo info = parseLine(line);
            if (info != null) {
                list.add(info);
            }
        }
        return list;
    }

    private ProcessInfo parseLine(String line) {
        String[] parts = line.trim().split("\t");
        if (parts.length < 14) {
            return null;
        }
        ProcessInfo info = new ProcessInfo();
        info.setPid(parseInt(parts[0]));
        info.setParentPid(parseInt(parts[1]));
        info.setUserName(parts[2].trim());
        info.setCpuUsage(parseDouble(parts[3]));
        info.setMemoryUsage(parseDouble(parts[4]));
        info.setVirtualMemory(parseLong(parts[5]));
        info.setResidentMemory(parseLong(parts[6]));
        info.setStatus(parts[7].trim());
        info.setPriority(parseInt(parts[8]));
        info.setNiceValue(parseInt(parts[9]));
        info.setStartTime(parts[10].trim());
        info.setCpuTime(parts[11].trim());
        info.setTerminal(parts[12].trim());
        String command = parts.length > 13 ? parts[13].trim() : "";
        info.setCommandLine(command);
        String[] tokens = command.split("\\s+");
        info.setName(tokens.length > 0 && !tokens[0].isEmpty() ? tokens[0] : command);
        return info;
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

    private Double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String shortPreview(String s) {
        if (s == null) {
            return "null";
        }
        return s.substring(0, Math.min(s.length(), 200));
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
