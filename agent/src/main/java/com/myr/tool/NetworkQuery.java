package com.myr.tool;

import com.myr.entity.NetworkInfo;
import com.myr.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 网络信息查询工具。
 * 通过调用 jssh-back-end 的 GET /network 接口，获取远程主机的网卡与网络流量信息。
 */
@Component
@RequiredArgsConstructor
public class NetworkQuery {

    private final RestClient restClient;

    @Tool(description = "查询远程主机的网络信息，返回所有网卡的名称、状态、MTU、MAC 地址、IPv4/IPv6 地址、收发字节数/包数/丢包数等。需先通过后端建立 SSH 连接。")
    public List<NetworkInfo> queryNetwork() {

        Result<List<NetworkInfo>> result = restClient.get()
                .uri("/network")
                .retrieve()
                .body(new ParameterizedTypeReference<Result<List<NetworkInfo>>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询网络信息失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}