package com.myr.controller;

import com.myr.entity.ProcessInfo;
import com.myr.entity.Result;
import com.myr.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @GetMapping
    public Result<List<ProcessInfo>> getProcessInfo() {
        return processService.getProcessInfo();
    }
}
