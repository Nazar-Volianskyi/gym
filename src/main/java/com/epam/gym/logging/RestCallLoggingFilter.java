package com.epam.gym.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Slf4j
public class RestCallLoggingFilter implements Filter {

    private static final Pattern SENSITIVE_FIELD_PATTERN =
            Pattern.compile("\"(password|oldPassword|newPassword)\"\\s*:\\s*\"[^\"]*\"");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 10_000);
        ContentCachingResponseWrapper cachedResponse = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(cachedRequest, cachedResponse);

            String requestBody = mask(new String(cachedRequest.getContentAsByteArray(), StandardCharsets.UTF_8));
            String responseBody = mask(new String(cachedResponse.getContentAsByteArray(), StandardCharsets.UTF_8));
            int status = cachedResponse.getStatus();

            log.info("{} {} | request: {} | status: {} | response: {}",
                    request.getMethod(), request.getRequestURI(), requestBody, status, responseBody);
        } finally {
            cachedResponse.copyBodyToResponse();
        }
    }

    private String mask(String body) {
        if (body == null || body.isBlank()) {
            return "-";
        }
        return SENSITIVE_FIELD_PATTERN.matcher(body).replaceAll("\"$1\":\"***\"");
    }
}
