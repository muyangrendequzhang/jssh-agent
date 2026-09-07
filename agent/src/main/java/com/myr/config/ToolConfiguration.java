package com.myr.config;

import com.myr.tool.ConnectionQuery;
import com.myr.tool.FileQuery;
import com.myr.tool.MemoryQuery;
import com.myr.tool.NetworkQuery;
import com.myr.tool.ProcessQuery;
import com.myr.tool.SystemQuery;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ToolConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Bean
    public ToolCallbackProvider myTools(ProcessQuery processQuery,
                                        MemoryQuery memoryQuery,
                                        SystemQuery systemQuery,
                                        NetworkQuery networkQuery,
                                        FileQuery fileQuery,
                                        ConnectionQuery connectionQuery) {
        // 新增工具类时，把带 @Tool 方法的对象追加到 toolObjects(...) 即可
        return MethodToolCallbackProvider.builder()
                .toolObjects(processQuery, memoryQuery, systemQuery, networkQuery, fileQuery, connectionQuery)
                .build();
    }
}
