package com.myr.tool;

import com.myr.entity.ProcessInfo;
import com.myr.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * 进程信息查询工具。
 * 通过调用 jssh-back-end 的 GET /process 接口，获取远程主机上的进程列表。
 */
@Component
@RequiredArgsConstructor
public class ProcessQuery {

    private final RestClient restClient;

    @Tool(description = "查询远程主机上的进程列表，返回所有进程的信息（包括 PID、父进程 PID、所属用户、进程名称、命令行、CPU/内存占用率、状态、启动时间等）。可配合 /base/connect 连接远程主机后使用。")
    public List<ProcessInfo> queryProcessInfo() {

        Result<List<ProcessInfo>> result = restClient.get()
                .uri("/process")
                .retrieve()
                .body(new ParameterizedTypeReference<Result<List<ProcessInfo>>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询进程信息失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}
