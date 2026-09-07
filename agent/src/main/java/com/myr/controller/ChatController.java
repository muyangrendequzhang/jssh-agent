package com.myr.controller;

import com.myr.agent.ChatService;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 以 SSE 流式方式调用 agent 对话。
     * 入参：{"message": "用户问题"}
     * 返回：text/event-stream，每个事件为一条 Message（增量文本块或工具完成事件）。
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> stream(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatService.stream(message);
    }
}