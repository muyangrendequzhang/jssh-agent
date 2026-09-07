package com.myr.tool;

import com.myr.entity.ConnectParam;
import com.myr.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 已保存连接查询工具。
 * 通过调用 jssh-back-end 的 GET /connection 接口，获取后端保存的 SSH 连接列表。
 */
@Component
@RequiredArgsConstructor
public class ConnectionQuery {

    private final RestClient restClient;

    @Tool(description = "查询后端已保存的 SSH 连接列表，返回每个连接的名称、主机、端口、用户名等。用于了解当前可用的远程主机连接。")
    public List<ConnectParam> queryConnections() {

        Result<List<ConnectParam>> result = restClient.get()
                .uri("/connection")
                .retrieve()
                .body(new ParameterizedTypeReference<Result<List<ConnectParam>>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询已保存连接失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}