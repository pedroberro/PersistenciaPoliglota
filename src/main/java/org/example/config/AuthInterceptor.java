package org.example.config;

import org.example.service.SesionService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SesionService sesionService;

    public AuthInterceptor(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Skip authentication for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/api/dashboard") ||
                path.startsWith("/api/sensors") ||
                path.startsWith("/api/measurements") ||
                path.startsWith("/api/financial") ||
                path.startsWith("/api/reports") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.equals("/") ||
                path.equals("/index.html") ||
                path.equals("/sensores") ||
                path.equals("/reportes") ||
                path.equals("/facturacion") ||
                path.equals("/health")) {
            return true;
        }

        // Check for token in header or parameter
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = request.getParameter("token");
        }

        if (token == null || !sesionService.validateSession(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token de autenticación requerido o inválido\"}");
            return false;
        }

        // Refresh session activity
        sesionService.refreshLastSeen(token);
        return true;
    }
}
