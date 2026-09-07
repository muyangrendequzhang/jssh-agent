package com.myr.tool;

import com.myr.entity.Result;
import com.myr.entity.SystemInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 系统服务信息查询工具。
 * 通过调用 jssh-back-end 的 GET /system 接口，获取远程主机的 systemd 服务状态列表。
 */
@Component
@RequiredArgsConstructor
public class SystemQuery {

    private final RestClient restClient;

    @Tool(description = "查询远程主机的系统服务列表，返回各 systemd 服务单元的名称、加载状态、激活状态、子状态及描述。需先通过后端建立 SSH 连接。")
    public List<SystemInfo> querySystemServices() {

        Result<List<SystemInfo>> result = restClient.get()
                .uri("/system")
                .retrieve()
                .body(new ParameterizedTypeReference<Result<List<SystemInfo>>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询系统服务信息失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}