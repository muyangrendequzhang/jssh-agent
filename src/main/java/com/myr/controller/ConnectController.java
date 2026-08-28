package com.myr.controller;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import com.myr.service.ConnectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("base")
public class ConnectController {

    @Autowired
    private  ConnectService connectService;





    @PostMapping("connect")
    public Result<String> makeConnection(@RequestBody ConnectParam param) {
        return connectService.connect(param);
    }
}
