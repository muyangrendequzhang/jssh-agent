package com.xmeport.agent.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {
    @Bean
    public ReactAgent myAgent(@Qualifier("dashScopeChatModel") ChatModel chatModel, ToolCallback[] myTools) {
        return ReactAgent.builder()
                // 1. 注入大模型
                .model(chatModel)
                // 2. 设定系统指令 (Instruction)
                .instruction("你是一个专业的编程助手，擅长解答Java和Spring相关的问题...")
                // 3. 注册工具集合 (Tools)
                .tools(myTools)
                // .build() 还有更多可选配置
                .build();
    }
}
