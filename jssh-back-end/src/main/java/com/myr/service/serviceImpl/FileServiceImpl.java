package com.myr.service.serviceImpl;

import com.myr.entity.FileStructure;
import com.myr.entity.Result;
import com.myr.service.ConnectService;
import com.myr.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {


    @Value("${local.store}")
    private String localPath;

    @Autowired
    private ConnectService connectService;

    private ClientSession getSession() {
        return connectService.getSession();
    }

    @Override
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

    @Override
    public void downloadFile(FileStructure fileStructure) {
        String remotePath = fileStructure.path;
        ClientSession session = getSession();
        if (session == null || !session.isOpen()) {
            throw new RuntimeException("未建立连接，请先调用 /connect");
        }
        try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session);
             InputStream remoteStream = sftp.read(remotePath);      // 远程文件流
             OutputStream localStream = Files.newOutputStream(
                     Paths.get(localPath).resolve(Paths.get(remotePath).getFileName().toString()))) { // 本地输出流

            // 流拷贝：从远程读到本地
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = remoteStream.read(buffer)) != -1) {
                localStream.write(buffer, 0, bytesRead);
            }
            localStream.flush();

            log.info("下载成功: {} -> {}", remotePath, localPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
