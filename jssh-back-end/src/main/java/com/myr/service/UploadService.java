package com.myr.service;

import com.myr.entity.ConnectParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UploadService {

    String uploadKey(MultipartFile file) throws IOException;

    void uploadConnection(ConnectParam param) throws IOException;

    ConnectParam readByName(String connectName) throws IOException;

    List<ConnectParam> listConnection() throws IOException;

    void deleteFile(String connectName) throws IOException;
}
