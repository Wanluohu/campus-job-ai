package org.example.mydemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final ConcurrentHashMap<String, List<Map<String, String>>> conversations;

    public ChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.conversations = new ConcurrentHashMap<>();
    }

    public Map<String, Object> sendMessage(String conversationId, String userMessage) {
        List<Map<String, String>> messages = conversations.computeIfAbsent(
                conversationId, k -> {
                    List<Map<String, String>> list = new ArrayList<>();
                    Map<String, String> systemMsg = new LinkedHashMap<>();
                    systemMsg.put("role", "system");
                    systemMsg.put("content", "你是一个有用的AI助手，名字叫Claude。请用中文回答用户的问题。");
                    list.add(systemMsg);
                    return list;
                });

        Map<String, String> userMsg = new LinkedHashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        // Send only recent messages to avoid token limit (keep system + last 20 messages)
        List<Map<String, String>> toSend;
        if (messages.size() > 22) {
            toSend = new ArrayList<>();
            toSend.add(messages.get(0)); // system message
            toSend.addAll(messages.subList(messages.size() - 20, messages.size()));
        } else {
            toSend = messages;
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", 4096);
        body.put("messages", toSend);

        try {
            String requestJson = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                messages.remove(messages.size() - 1);
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("reply", "API 调用失败: HTTP " + response.statusCode() + " - " + response.body());
                result.put("role", "error");
                return result;
            }

            JsonNode root = objectMapper.readTree(response.body());
            String replyText = root.path("choices").get(0)
                    .path("message").path("content").asText();

            Map<String, String> assistantMsg = new LinkedHashMap<>();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", replyText);
            messages.add(assistantMsg);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("reply", replyText);
            result.put("role", "assistant");
            return result;

        } catch (Exception e) {
            messages.remove(messages.size() - 1);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("reply", "请求失败: " + e.getMessage());
            result.put("role", "error");
            return result;
        }
    }

    public List<Map<String, Object>> getConversations() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String id : conversations.keySet()) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", id);
            List<Map<String, String>> msgs = conversations.get(id);
            if (msgs != null && msgs.size() > 1) {
                Map<String, String> firstMsg = msgs.get(1);
                String title = firstMsg.getOrDefault("content", "新对话");
                info.put("title", title.length() > 30 ? title.substring(0, 30) + "..." : title);
                info.put("messageCount", msgs.size() - 1);
            } else {
                info.put("title", "新对话");
                info.put("messageCount", 0);
            }
            list.add(info);
        }
        return list;
    }

    public boolean deleteConversation(String conversationId) {
        return conversations.remove(conversationId) != null;
    }
}
