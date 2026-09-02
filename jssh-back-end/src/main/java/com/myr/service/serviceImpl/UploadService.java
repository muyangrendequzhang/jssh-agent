package com.myr.service.serviceImpl;

import com.myr.utils.IoUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadService {


    public String uploadKey(MultipartFile file) throws IOException {

        String filename = UUID.randomUUID().toString();
        Path target = Paths.get("..", "jssh-restore", "key", filename + ".pem");
        IoUtils.write(target, new String(file.getBytes(), StandardCharsets.UTF_8));
        return target.toAbsolutePath().normalize().toString();
    }
}
