package com.banking.account.infrastructure.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class MockClientHttpResponse implements ClientHttpResponse {

    private final String body;
    private final int statusCode;

    MockClientHttpResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return HttpStatusCode.valueOf(statusCode);
    }

    @Override
    public String getStatusText() throws IOException {
        return String.valueOf(statusCode);
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }
}