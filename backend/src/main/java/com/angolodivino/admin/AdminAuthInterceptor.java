package com.angolodivino.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Hand-rolled bearer-token check for /api/admin/**, in place of Spring Security.
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminProperties adminProperties;
    private final AdminTokenStore tokenStore;
    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(AdminProperties adminProperties, AdminTokenStore tokenStore,
            ObjectMapper objectMapper) {
        this.adminProperties = adminProperties;
        this.tokenStore = tokenStore;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // Preflight carries no Authorization header; let the CORS handler answer it.
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        if (!adminProperties.isEnabled()) {
            reject(response, HttpStatus.SERVICE_UNAVAILABLE, "admin_disabled",
                    "Pannello admin non configurato: manca ADMIN_PASSWORD.");
            return false;
        }

        String token = AdminTokenStore.bearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (tokenStore.validate(token).isEmpty()) {
            reject(response, HttpStatus.UNAUTHORIZED, "unauthorized", "Sessione admin non valida o scaduta.");
            return false;
        }

        return true;
    }

    private void reject(HttpServletResponse response, HttpStatus status, String error, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new AdminApiError(error, message));
    }
}
