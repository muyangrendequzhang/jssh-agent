package com.myr.controller;

import com.myr.entity.FileStructure;
import com.myr.entity.Result;
import com.myr.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("file")
@RestController
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping
    public Result<FileStructure> getFile(FileStructure structure) {
        return fileService.getFileNames(structure);
    }
}
