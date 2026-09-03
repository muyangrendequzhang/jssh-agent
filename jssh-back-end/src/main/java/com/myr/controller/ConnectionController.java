package com.myr.controller;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import com.myr.service.ConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 该类用来已经完成的ssh连接
 */
@RestController
@Slf4j
@RequestMapping("connection")
public class ConnectionController {

    @Autowired
    public ConnectionService connectionService;


    @GetMapping
    public Result<List<ConnectParam>> getConnections() throws IOException {
        List<ConnectParam> list = connectionService.getConnections();
        return Result.success(list);
    }

    @DeleteMapping("/{connectName}")
    public Result<String> deleteConnection(@PathVariable("connectName") String connectName) throws IOException {
        connectionService.deleteConnection(connectName);
        return Result.success("成功删除");
    }
}
