package org.example.mydemo.controller;

import org.example.mydemo.other.Result;
import org.example.mydemo.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public Result sendMessage(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.trim().isEmpty()) {
            Result r = new Result();
            r.setCode(400);
            r.setMsg("消息不能为空");
            return r;
        }

        String conversationId = body.getOrDefault("conversationId",
                UUID.randomUUID().toString().replace("-", ""));

        Map<String, Object> reply = chatService.sendMessage(conversationId, message.trim());

        Result r = new Result();
        if ("error".equals(reply.get("role"))) {
            r.setCode(500);
            r.setMsg((String) reply.get("reply"));
        } else {
            r.setCode(200);
            r.setMsg("success");
            reply.put("conversationId", conversationId);
            r.setData(reply);
        }
        return r;
    }

    @GetMapping("/conversations")
    public Result listConversations() {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("success");
        r.setData(chatService.getConversations());
        return r;
    }

    @DeleteMapping("/conversations/{id}")
    public Result deleteConversation(@PathVariable String id) {
        boolean deleted = chatService.deleteConversation(id);
        Result r = new Result();
        if (deleted) {
            r.setCode(200);
            r.setMsg("删除成功");
        } else {
            r.setCode(404);
            r.setMsg("会话不存在");
        }
        return r;
    }

    @DeleteMapping("/reset")
    public Result resetConversation(@RequestParam(defaultValue = "") String conversationId) {
        if (!conversationId.isEmpty()) {
            chatService.deleteConversation(conversationId);
        }
        Result r = new Result();
        r.setCode(200);
        r.setMsg("会话已重置");
        return r;
    }
}
