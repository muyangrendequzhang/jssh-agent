package com.myr.controller;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;
import com.myr.service.MemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("memory")
@RestController
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    @GetMapping
    public Result<MemoryInfo> getMemory() {
        return memoryService.getMemory();
    }
}
