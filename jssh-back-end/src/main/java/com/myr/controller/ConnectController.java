package com.myr.controller;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import com.myr.service.ConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/base")
public class ConnectController {

    @Autowired
    private  ConnectService connectService;


    @PostMapping("connect")
    public Result<String> makeConnection(@RequestBody ConnectParam param) {
        log.debug("连接参数：{}", param.toString());
        return connectService.connect(param);
    }
}
