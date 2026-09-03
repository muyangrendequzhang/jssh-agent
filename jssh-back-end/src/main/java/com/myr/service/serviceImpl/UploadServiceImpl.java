package com.myr.service.serviceImpl;

import com.myr.entity.ConnectParam;
import com.myr.service.UploadService;
import com.myr.utils.IoUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UploadServiceImpl implements UploadService {

    private static final Path CONNECTION_DIR = Paths.get("..", "jssh-restore", "connection");
    private static final String CONNECTION_SUFFIX = ".txt";

    @Override
    public String uploadKey(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID().toString();
        Path target = Paths.get("..", "jssh-restore", "key", filename + ".pem");
        IoUtils.write(target, new String(file.getBytes(), StandardCharsets.UTF_8));
        return target.toAbsolutePath().normalize().toString();
    }

    @Override
    public void uploadConnection(ConnectParam param) throws IOException {
        Path file = connectionFile(param.getConnectName());
        IoUtils.saveConnection(toInfo(param), file);
    }

    @Override
    public ConnectParam readByName(String connectName) throws IOException {
        IoUtils.ConnectionInfo info = IoUtils.loadConnection(connectionFile(connectName));
        return info == null ? null : toParam(connectName, info);
    }

    @Override
    public List<ConnectParam> listConnection() throws IOException {
        List<ConnectParam> result = new ArrayList<>();
        if (!Files.exists(CONNECTION_DIR)) {
            return result;
        }
        try (Stream<Path> files = Files.list(CONNECTION_DIR)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                String name = file.getFileName().toString();
                if (!name.endsWith(CONNECTION_SUFFIX)) {
                    continue;
                }
                IoUtils.ConnectionInfo info = IoUtils.loadConnection(file);
                if (info == null) {
                    continue;
                }
                String connectName = name.substring(0, name.length() - CONNECTION_SUFFIX.length());
                result.add(toParam(connectName, info));
            }
        }
        return result;
    }

    @Override
    public void deleteFile(String connectName) throws IOException {
        Path resolve = CONNECTION_DIR.resolve(connectName + CONNECTION_SUFFIX);
        IoUtils.delete(resolve);
    }

    private Path connectionFile(String connectName) {
        return CONNECTION_DIR.resolve(connectName + CONNECTION_SUFFIX);
    }

    private IoUtils.ConnectionInfo toInfo(ConnectParam param) {
        IoUtils.ConnectionInfo info = new IoUtils.ConnectionInfo();
        info.setHost(param.getHost());
        info.setPort(param.getPort());
        info.setUser(param.getUser());
        info.setPassword(param.getPassword());
        info.setPrivateKeyPath(param.getPrivateKeyPath());
        return info;
    }

    private ConnectParam toParam(String connectName, IoUtils.ConnectionInfo info) {
        ConnectParam param = new ConnectParam();
        param.setConnectName(connectName);
        param.setHost(info.getHost());
        param.setPort(info.getPort());
        param.setUser(info.getUser());
        param.setPassword(info.getPassword());
        param.setPrivateKeyPath(info.getPrivateKeyPath());
        return param;
    }
}
