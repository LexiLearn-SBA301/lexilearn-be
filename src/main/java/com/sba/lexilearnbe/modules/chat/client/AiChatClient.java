package com.sba.lexilearnbe.modules.chat.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Client gọi AI service (rag-service). BE là bên DUY NHẤT gọi AI (FE không đụng) -> auth
 * tập trung ở BE. Dùng java.net.http.HttpClient (JDK) để không thêm dependency; stream đọc
 * theo dòng (SSE) và forward realtime.
 */
@Component
public class AiChatClient {

    private static final Logger log = LoggerFactory.getLogger(AiChatClient.class);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public AiChatClient(ObjectMapper objectMapper,
                        @Value("${app.ai.base-url:http://localhost:8000}") String baseUrl) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
    }

    /** Checkpoint của thread còn lịch sử không -> BE quyết có seed hay không. Lỗi -> coi như chưa có. */
    public boolean threadExists(UUID threadId) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/exists/" + threadId))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 400) {
                return false;
            }
            return objectMapper.readTree(resp.body()).path("exists").asBoolean(false);
        } catch (Exception e) {
            log.warn("AI /chat/exists lỗi ({}) -> coi như chưa tồn tại, sẽ seed.", e.getMessage());
            return false;
        }
    }

    /** Nạp lịch sử (cũ -> mới, tối đa 10) vào checkpoint AI. Best-effort: lỗi chỉ log. */
    public void seed(UUID threadId, List<Map<String, String>> history) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "thread_id", threadId.toString(),
                    "history", history));
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/seed"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 400) {
                log.warn("AI /chat/seed trả {}: {}", resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            log.warn("AI /chat/seed lỗi: {}", e.getMessage());
        }
    }

    /**
     * Stream câu trả lời từ AI. Forward MỖI event (JSON 1 dòng) qua onEvent để BE relay xuống
     * FE realtime; song song bắt event type=done để lấy câu trả lời cuối lưu DB. Trả answer cuối.
     */
    public String stream(UUID threadId, String message, Consumer<String> onEvent)
            throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(Map.of(
                "thread_id", threadId.toString(),
                "message", message));
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/stream"))
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<Stream<String>> resp = http.send(req, HttpResponse.BodyHandlers.ofLines());
        if (resp.statusCode() >= 400) {
            throw new IOException("AI /chat/stream HTTP " + resp.statusCode());
        }

        String answer = "";
        Iterator<String> it = resp.body().iterator();     // đọc lazy -> forward gần realtime
        while (it.hasNext()) {
            String line = it.next();
            if (!line.startsWith("data:")) {
                continue;
            }
            String json = line.substring(5).trim();
            if (json.isEmpty()) {
                continue;
            }
            onEvent.accept(json);                          // chuyền tay xuống FE
            try {
                JsonNode node = objectMapper.readTree(json);
                if ("done".equals(node.path("type").asText())) {
                    answer = node.path("payload").path("answer").asText("");
                }
            } catch (Exception ignore) {
                // dòng không parse được -> đã forward, bỏ qua để không vỡ stream
            }
        }
        return answer;
    }
}
