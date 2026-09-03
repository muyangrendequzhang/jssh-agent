package com.myr.service.serviceImpl;

import com.myr.entity.ConnectParam;
import com.myr.utils.IoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class ConnectionService {

    @Autowired
    private UploadService uploadService;

    public List<ConnectParam> getConnections() throws IOException {
        List<ConnectParam> list = uploadService.listConnection();
        log.info("读取磁盘中信息{}",list);
        return list;
    }

    public void deleteConnection(String connectName) throws IOException {
        ConnectParam connectParam = uploadService.readByName(connectName);
        if (connectParam == null) {
            return;
        }
        String privateKeyPath = connectParam.getPrivateKeyPath();
        if (privateKeyPath != null && !privateKeyPath.isBlank()) {
            IoUtils.delete(Paths.get(privateKeyPath));
        }
        uploadService.deleteFile(connectName);
    }
}
