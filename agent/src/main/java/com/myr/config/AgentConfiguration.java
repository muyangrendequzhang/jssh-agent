package com.myr.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {
    @Bean(name = "chatModel")
    public ReactAgent myAgent(@Qualifier("deepSeekChatModel") ChatModel chatModel, ToolCallbackProvider myTools) {
        return ReactAgent.builder()
                // 0. agent 名称（必填）
                .name("chatAgent")
                // 1. 注入大模型
                .model(chatModel)
                // 2. 设定系统指令 (Instruction)
                .instruction("你是一个专业的编程助手，擅长解答Java和Spring相关的问题...")
                // 3. 注册工具集合 (Tools)：从 ToolCallbackProvider 获取所有工具
                .toolCallbackProviders(myTools)
                //使用自带的记忆存储
                .saver(new MemorySaver())
                // .build() 还有更多可选配置
                .build();
    }
}
