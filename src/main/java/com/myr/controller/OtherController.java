package com.myr.controller;

import com.myr.entity.*;
import com.myr.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController("other")
public class OtherController {

    @Autowired
    private OtherService otherService;

    @GetMapping("memory")
    public Result<MemoryInfo> getMemory() {
        return otherService.getMemory();
    }

    @GetMapping("file")
    public Result<FileStructure> getFile(FileStructure structure) {
        return otherService.getFileNames(structure);
    }

    @GetMapping("process")
    public Result<List<ProcessInfo>> getProcessInfo() {
        return otherService.getProcessInfo();
    }

    @GetMapping("network")
    public Result<List<NetworkInfo>> getNetwork() {
        return otherService.getNetworkInfo();
    }
}
