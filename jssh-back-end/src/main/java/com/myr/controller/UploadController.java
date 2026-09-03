package com.myr.controller;

import com.myr.entity.Result;
import com.myr.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
public class UploadController {

    @Autowired
    public UploadService uploadService;

    @PostMapping("uploadKey")
    public Result<String> uploadKey(@RequestParam("file") MultipartFile file){
        try {
            String path = uploadService.uploadKey(file);
            log.debug("存储密钥的路径是{}", path);
            return Result.success(path);
        } catch (IOException e) {
            log.error("上传密钥失败", e);
            return Result.fail("上传密钥失败");
        }
    }
}
