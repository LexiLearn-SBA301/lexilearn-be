package com.sba.lexilearnbe.modules.work.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AiWorkSyncClient {

    private static final Logger log = LoggerFactory.getLogger(AiWorkSyncClient.class);

    private final ObjectMapper objectMapper;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${app.ai.base-url:http://localhost:8000}")
    private String baseUrl;

    public void upsertWork(WorkSyncPayload payload) {
        try {
            String workSlug = payload.work().slug();
            String body = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder(syncUri(workSlug))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                log.warn("AI work sync upsert failed workSlug={} status={} body={}",
                        workSlug, response.statusCode(), response.body());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("AI work sync upsert interrupted workSlug={}", payload.work().slug());
        } catch (Exception exception) {
            log.warn("AI work sync upsert error workSlug={}: {}", payload.work().slug(), exception.getMessage());
        }
    }

    public void deleteWork(String workSlug) {
        try {
            HttpRequest request = HttpRequest.newBuilder(syncUri(workSlug))
                    .timeout(Duration.ofSeconds(10))
                    .DELETE()
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 400) {
                log.warn("AI work sync delete failed workSlug={} status={} body={}",
                        workSlug, response.statusCode(), response.body());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.warn("AI work sync delete interrupted workSlug={}", workSlug);
        } catch (Exception exception) {
            log.warn("AI work sync delete error workSlug={}: {}", workSlug, exception.getMessage());
        }
    }

    private URI syncUri(String workSlug) {
        String encodedSlug = URLEncoder.encode(workSlug, StandardCharsets.UTF_8);
        return URI.create(baseUrl + "/internal/works/" + encodedSlug + "/sync");
    }
}
