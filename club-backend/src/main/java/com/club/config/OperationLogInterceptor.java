package com.club.config;

import com.club.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OperationLogInterceptor implements HandlerInterceptor {

    private final OperationLogService operationLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("auditStartTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        if (!isWriteMethod(method) || request.getRequestURI().startsWith("/api/logs")) {
            return;
        }

        String username = (String) request.getAttribute("username");
        Object userId = request.getAttribute("userId");
        String module = resolveModule(request.getRequestURI());
        String status = ex == null && response.getStatus() < 400 ? "success" : "failed";
        String description = buildDescription(request, response, ex, username, userId);

        try {
            operationLogService.record(
                    username,
                    module,
                    method,
                    description,
                    clientIp(request),
                    request.getHeader("User-Agent"),
                    status
            );
        } catch (Exception ignored) {
            // Logging must not affect the business operation response.
        }
    }

    private boolean isWriteMethod(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method) || "PATCH".equals(method);
    }

    private String resolveModule(String uri) {
        String path = uri == null ? "" : uri.replaceFirst("^/api/?", "");
        if (path.isBlank()) {
            return "system";
        }
        int slash = path.indexOf('/');
        return slash > 0 ? path.substring(0, slash) : path;
    }

    private String buildDescription(HttpServletRequest request, HttpServletResponse response, Exception ex, Object username, Object userId) {
        StringBuilder description = new StringBuilder();
        description.append(request.getMethod()).append(' ').append(request.getRequestURI());
        if (username != null) {
            description.append(" | operator=").append(username);
        }
        String query = request.getQueryString();
        if (query != null && !query.isBlank()) {
            description.append('?').append(query);
        }
        String objectType = resolveModule(request.getRequestURI());
        if (objectType != null) {
            description.append(" | objectType=").append(objectType);
        }
        String objectId = resolveObjectId(request.getRequestURI());
        if (objectId != null) {
            description.append(" | objectId=").append(objectId);
        }
        if (userId != null) {
            description.append(" | operatorId=").append(userId);
        }
        description.append(" | httpStatus=").append(response.getStatus());
        Object start = request.getAttribute("auditStartTime");
        if (start instanceof Long startTime) {
            description.append(" | elapsedMs=").append(System.currentTimeMillis() - startTime);
        }
        if (ex != null) {
            description.append(" | failureType=").append(ex.getClass().getSimpleName());
            if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
                description.append(" | failure=").append(ex.getMessage());
            }
        }
        String value = description.toString();
        return value.length() > 500 ? value.substring(0, 500) : value;
    }

    private String resolveObjectId(String uri) {
        if (uri == null || uri.isBlank()) {
            return null;
        }
        String[] parts = uri.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i].matches("\\d+")) {
                return parts[i];
            }
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
