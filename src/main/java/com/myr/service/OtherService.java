package com.myr.service;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;
import com.myr.service.serviceImpl.MemroyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public Result<MemoryInfo> getMemory() {
        log.debug("调用获取memory具体实现");
       return memroyService.getMemory();
    }


}
