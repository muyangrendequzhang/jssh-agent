package com.myr.controller;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;
import com.myr.service.OtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController("other")
public class OtherController {

    @Autowired
    private OtherService otherService;

    @GetMapping("memory")
    public Result<MemoryInfo> getMemory() {
        return otherService.getMemory();
    }


}
