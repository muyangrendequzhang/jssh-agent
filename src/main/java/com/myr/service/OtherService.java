package com.myr.service;

import com.myr.entity.*;
import com.myr.service.serviceImpl.FileService;
import com.myr.service.serviceImpl.MemroyService;
import com.myr.service.serviceImpl.NetworkService;
import com.myr.service.serviceImpl.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用 Shell 通道，保持长连接保证内存等数据的一致性
 */
@Slf4j
@Service
public class OtherService {



    @Autowired
    private ConnectService connectService;

    @Autowired
    private MemroyService memroyService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private NetworkService networkService;

    public Result<MemoryInfo> getMemory() {
        log.debug("调用获取memory具体实现");
       return memroyService.getMemory();
    }


    public Result<FileStructure> getFileNames(FileStructure structure) {
        return fileService.getFileNames(structure);
    }

    public Result<List<ProcessInfo>> getProcessInfo() {
        return processService.getProcessInfo();
    }

    public Result<List<NetworkInfo>> getNetworkInfo() {
        return networkService.getNetworkInfo();
    }
}
