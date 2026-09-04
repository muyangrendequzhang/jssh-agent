package com.myr.controller;

import com.myr.entity.Result;
import com.myr.entity.SystemInfo;
import com.myr.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("system")
@RestController
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping
    public Result<List<SystemInfo>> getSystemInfo() {
        return systemService.getSystemInfo();
    }
}
