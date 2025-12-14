package ru.practicum.shareit.gateway.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class BaseClient {
    protected final RestTemplate rest;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${shareit.server.url}")
    protected String serverUrl;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, buildUrl(path), null, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return makeAndSendRequest(HttpMethod.GET, buildUrl(path), userId, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.GET, buildUrl(path), userId, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, buildUrl(path), userId, parameters, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, buildUrl(path), null, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), null, null, body);
    }

    protected ResponseEntity<Object> post(String path, long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, parameters, body);
    }

    protected ResponseEntity<Object> patch(String path, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), null, null, body);
    }

    protected ResponseEntity<Object> patch(String path, long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, parameters, body);
    }

    protected ResponseEntity<Object> put(String path, long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> put(String path, Long userId, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, null, body);
    }

    protected ResponseEntity<Object> put(String path, Long userId, @Nullable Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, buildUrl(path), null, null, null);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, buildUrl(path), userId, null, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, buildUrl(path), userId, null, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, buildUrl(path), userId, parameters, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String url, @Nullable Long userId,
                                                      @Nullable Map<String, Object> parameters,
                                                      @Nullable Object body) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, createHeaders(userId));

        log.debug("Making {} request to {} with userId: {}, body: {}",
                method, url, userId, body);

        try {
            ResponseEntity<Object> response;
            if (parameters != null) {
                response = rest.exchange(url, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(url, method, requestEntity, Object.class);
            }

            log.debug("Success response from {}: {}", url, response.getStatusCode());
            return response;

        } catch (HttpStatusCodeException e) {
            log.error("HTTP error from {}: {} - Body: {}", url, e.getStatusCode(),
                    e.getResponseBodyAsString());

            try {
                Object errorBody = objectMapper.readValue(e.getResponseBodyAsString(), Object.class);
                return ResponseEntity.status(e.getStatusCode()).body(errorBody);
            } catch (Exception ex) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
            }
        } catch (Exception e) {
            log.error("Unexpected error making request to {}: {}", url, e.getMessage(), e);
            throw e;
        }
    }

    private HttpHeaders createHeaders(@Nullable Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private String buildUrl(String path) {
        return serverUrl + path;
    }
}