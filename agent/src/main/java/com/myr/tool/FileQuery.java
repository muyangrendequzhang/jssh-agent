package com.myr.tool;

import com.myr.entity.FileStructure;
import com.myr.entity.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 文件目录查询工具。
 * 通过调用 jssh-back-end 的 GET /file 接口，获取远程主机指定路径下的文件/目录列表。
 */
@Component
@RequiredArgsConstructor
public class FileQuery {

    private final RestClient restClient;

    @Tool(description = "查询远程主机指定路径下的文件/目录列表（一层，不递归）。返回该路径下的子项名称与完整路径。需先通过后端建立 SSH 连接。")
    public FileStructure queryFiles(
            @ToolParam(description = "远程目录路径，如 /home/user 或 /etc") String path) {

        Result<FileStructure> result = restClient.get()
                .uri("/file?path={path}", path)
                .retrieve()
                .body(new ParameterizedTypeReference<Result<FileStructure>>() {
                });

        if (result == null || !result.isSuccess()) {
            throw new IllegalStateException("查询文件列表失败: " + (result == null ? "无响应" : result.getMessage()));
        }

        return result.getData();
    }
}