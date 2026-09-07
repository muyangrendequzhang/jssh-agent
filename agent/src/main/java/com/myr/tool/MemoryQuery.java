package com.myr.tool;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 内存信息查询工具。
 * 通过调用 jssh-back-end 的 GET /memory 接口，获取远程主机的内存使用情况。
 */
@Component
@RequiredArgsConstructor
public class MemoryQuery {

    private final RestClient restClient;

    @Tool(description = "查询远程主机的内存信息，返回总物理内存、已用/可用/空闲内存、缓冲与缓存、交换分区、内存使用率等。需先通过后端建立 SSH 连接。")
    public MemoryInfo queryMemory() {

        Result<MemoryInfo> result = restClient.get()
                .uri("/memory")
                .retrieve()
                .body(new ParameterizedTypeReference<Result<MemoryInfo>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询内存信息失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}