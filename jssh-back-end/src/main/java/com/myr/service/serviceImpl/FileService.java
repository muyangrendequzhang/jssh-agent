package com.myr.service.serviceImpl;

import com.myr.entity.FileStructure;
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
import java.util.List;

@Slf4j
@Service
public class FileService {

    @Autowired
    private ConnectService connectService;

    public ClientSession getSession() {
        return connectService.getSession();
    }

    public Result<FileStructure> getFileNames(FileStructure structure) {
        String path = structure.path;
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            return Result.fail("未建立连接，请先调用 /connect");
        }
        try {
            structure.setChildrenFiles(listDirectory(session, path));
            return Result.success(structure);
        } catch (Exception e) {
            return Result.fail("查询文件失败: " + e.getMessage());
        }
    }

    /**
     * 列出一个目录下的直接子项（只有一层），不递归。
     */
    private List<FileStructure> listDirectory(ClientSession session, String path) throws Exception {
        if (path == null || path.isBlank()) {
            return new ArrayList<>();
        }
        String cleanPath = path.endsWith("/") ? path : path + "/";
        String command = "find \"" + cleanPath + "\" -maxdepth 1 -mindepth 1 -printf '%f\\n'";
        String output = exec(session, command);

        List<FileStructure> children = new ArrayList<>();
        if (output == null || output.isBlank()) {
            return children;
        }
        for (String line : output.split("\\r?\\n")) {
            String name = line.trim();
            if (name.isEmpty()) {
                continue;
            }
            FileStructure child = new FileStructure();
            child.setName(name);
            child.setPath(cleanPath + name);
            child.setChildrenFiles(new ArrayList<>());
            children.add(child);
        }
        return children;
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
