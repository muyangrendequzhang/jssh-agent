package com.myr.agent;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {
    private final ReactAgent chatModel;

    public ChatService(@Qualifier("chatModel") ReactAgent chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 以流式方式调用 agent，返回用户可见的消息流（模型增量文本块与工具完成事件）。
     */
    public Flux<Message> stream(String message) {
        try {
            return chatModel.streamMessages(message);
        } catch (Exception e) {
            return Flux.error(new RuntimeException("调用 agent 失败: " + e.getMessage(), e));
        }
    }
}
