package com.myr.controller;

import com.myr.entity.NetworkInfo;
import com.myr.entity.Result;
import com.myr.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("network")
@RestController
public class NetworkController {

    @Autowired
    private NetworkService networkService;

    @GetMapping
    public Result<List<NetworkInfo>> getNetwork() {
        return networkService.getNetworkInfo();
    }
}
