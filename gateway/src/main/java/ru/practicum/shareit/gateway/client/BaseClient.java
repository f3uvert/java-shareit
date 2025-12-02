package ru.practicum.shareit.gateway.client;

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

    @Value("${shareit.server.url}")
    protected String serverUrl;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    // GET методы
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

    // POST методы
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, buildUrl(path), userId, parameters, body);
    }

    // PATCH методы
    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, buildUrl(path), userId, parameters, body);
    }

    // PUT методы
    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, Long userId, T body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, buildUrl(path), userId, parameters, body);
    }

    // DELETE методы
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

    // Основной метод для отправки запросов
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String url, @Nullable Long userId,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, createHeaders(userId));

        log.debug("Making {} request to {} with userId: {}, params: {}",
                method, url, userId, parameters);

        ResponseEntity<Object> response;
        try {
            if (parameters != null) {
                response = rest.exchange(url, method, requestEntity, Object.class, parameters);
            } else {
                response = rest.exchange(url, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error making request to {}: {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        } catch (Exception e) {
            log.error("Unexpected error making request to {}: {}", url, e.getMessage(), e);
            throw e;
        }

        log.debug("Response from {}: {}", url, response.getStatusCode());
        return prepareResponse(response);
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

    private ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    private String buildUrl(String path) {
        return serverUrl + path;
    }
}