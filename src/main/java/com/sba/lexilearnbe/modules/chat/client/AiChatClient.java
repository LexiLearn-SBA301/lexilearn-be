package com.sba.lexilearnbe.modules.chat.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba.lexilearnbe.modules.chat.enums.ChatModel;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
     * Stream câu trả lời từ AI (SSE). Trả answer cuối (từ event type=done) để relay lưu DB.
     *
     * Hai điểm CHỦ ĐÍCH:
     *  1) Parse `done.answer` TRƯỚC khi forward xuống FE -> chốt được câu trả lời KHÔNG phụ thuộc
     *     việc gửi FE có lỗi hay không. FE rớt tạm thời (onEvent ném) -> ngừng forward nhưng VẪN đọc
     *     hết stream để relay lưu được bài đã sinh (không mất bài đã tốn compute).
     *  2) Dùng ofInputStream + giữ InputStream trong `session` -> /stop có thể ĐÓNG kết nối BE↔AI
     *     (huỷ Ollama). Khi bị đóng do /stop, readLine bung IOException -> trả "" (không lưu).
     */
    public String stream(UUID threadId, String message, Consumer<String> onEvent, StreamSession session)
            throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(Map.of(
                "thread_id", threadId.toString(),
                "message", message));
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/stream"))
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
        if (resp.statusCode() >= 400) {
            resp.body().close();
            throw new IOException("AI /chat/stream HTTP " + resp.statusCode());
        }

        InputStream is = resp.body();
        session.attach(is);
        // is là là kiểu InputStream nhưng có thể gắn vào hàm attach nhận kiểu Closeable
        // -> vì theo quy tắc kế thừa class InputStream implements Closeable
        // -> Hàm đối tượng cha -> đối tượng con kế thừa -> có thể truyền kiểu cha vào kiểu con - ( ngược lại lỗi)

        String answer = "";
        boolean clientGone = false;   // FE rớt -> ngừng forward nhưng vẫn đọc nốt để chốt answer
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data:")) {
                    continue;
                }
                String json = line.substring(5).trim();
                if (json.isEmpty()) {
                    continue;
                }
                // (1) Parse TRƯỚC khi forward -> answer cuối không phụ thuộc việc gửi FE.
                try {
                    JsonNode node = objectMapper.readTree(json);
                    if ("done".equals(node.path("type").asText())) {
                        answer = node.path("payload").path("answer").asText("");
                    }
                } catch (Exception ignore) {
                    // dòng không parse được -> bỏ qua, vẫn forward bên dưới
                }
                // Forward best-effort: FE rớt -> set cờ, ngừng gửi nhưng tiếp tục đọc để lưu được.
                if (!clientGone) {
                    try {
                        onEvent.accept(json);
                    } catch (RuntimeException e) {
                        clientGone = true;
                        log.warn("FE rớt giữa stream thread={} -> đọc nốt để lưu final message.", threadId);
                    }
                }
            }
        } catch (IOException e) {
            // (2) /stop đóng kết nối -> readLine bung IOException. Đó là huỷ CHỦ ĐỘNG, không phải lỗi.
            if (session.isCancelled()) {
                return "";   // không có answer để lưu -> relay bỏ qua ai_message
            }
            throw e;         // lỗi đọc thật -> để relay xử lý (báo error xuống FE)
        }
        return answer;
    }

    //Báo AI: người học muốn tranh luận cùng hội đồng ở lượt ĐANG run
    public void debateOptin(UUID threadId) {
        try {
            String body = objectMapper.writeValueAsString(Map.of("thread_id", threadId.toString()));
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/debate/optin"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 400) {
                log.warn("AI /chat/debate/optin trả {}: {}", resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            log.warn("AI /chat/debate/optin lỗi: {}", e.getMessage());
        }
    }

    public void debateReply(UUID threadId, String message, String targetArgId, String stance) {
        Map<String, Object> body = new HashMap<>();
        body.put("thread_id", threadId.toString());
        // HashMap (không phải Map.of) vì 3 field dưới được phép null: Map.of ném NPE với null,
        // mà "message=null" chính là tín hiệu Bỏ qua/Kết thúc — không thể bỏ field đi.
        body.put("message", message);
        body.put("target_arg_id", targetArgId);
        body.put("stance", stance);

        HttpResponse<String> resp;
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/chat/debate/reply"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(body), StandardCharsets.UTF_8))
                    .build();
            resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException e) { //isInterrupted() == false
            // JVM tự gán isInterrupted cho mình xử lý -> nếu không gán lại thì logic phía trên vẫn chạy tiếp
            Thread.currentThread().interrupt();
            // gán lại isInterrupted() == true -> các hàm CÓ KHAI BÁO throws InterruptedException
            // (sleep/take/http.send...) sẽ nổ ngay. Còn readLine()/JDBC thì KHÔNG quan tâm cờ này
            throw new ApiException(ErrorCode.CHAT_AI_UNAVAILABLE); // throw vẫn chạy được vì chỉ đơn giản là ném chứ không block io
        } catch (IOException e) {
            throw new ApiException(ErrorCode.CHAT_AI_UNAVAILABLE);
        }

        if (resp.statusCode() == 409) {
            throw new ApiException(ErrorCode.DEBATE_NOT_WAITING);
        }
        if (resp.statusCode() == 400) {
            throw new ApiException(ErrorCode.DEBATE_INVALID_REPLY, readDetail(resp.body()));
        }
        if (resp.statusCode() >= 400) {
            log.warn("AI /chat/debate/reply trả {}: {}", resp.statusCode(), resp.body());
            throw new ApiException(ErrorCode.CHAT_AI_UNAVAILABLE);
        }
    }

    /** Bóc {"detail": "..."} của FastAPI; không parse được thì trả null (dùng message mặc định). */
    private String readDetail(String body) {
        try {
            String detail = objectMapper.readTree(body).path("detail").asText("");
            return detail.isBlank() ? null : detail;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gọi model đơn (only-llm / base-llm) ĐỒNG BỘ, không stream, không dùng thread_id/checkpoint.
     * Chỉ gửi message (không gửi history) -> AI trả lời một-lượt. Dùng cho luồng lưu-lịch-sử
     * không nhớ ngữ cảnh. Trả về câu trả lời + sources (sources để FE hiển thị, BE không lưu).
     */
    public AiAnswer query(ChatModel model, String message) throws IOException, InterruptedException {
        String body = objectMapper.writeValueAsString(Map.of("message", message));
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + model.aiPath()))
                .timeout(Duration.ofSeconds(120))     // RAG + LLM có thể lâu
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 400) {
            throw new IOException("AI " + model.aiPath() + " HTTP " + resp.statusCode());
        }

        JsonNode node = objectMapper.readTree(resp.body());
        String answer = node.path("answer").asText("");
        List<Object> sources = node.path("sources").isArray()
                ? objectMapper.convertValue(node.get("sources"), new TypeReference<List<Object>>() {})
                : List.of();
        return new AiAnswer(answer, sources);
    }

    /** Kết quả 1 lượt gọi model đơn. */
    public record AiAnswer(String answer, List<Object> sources) {
    }
}
