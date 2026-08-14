package mintur.serviciomedico.util;

import jakarta.servlet.http.HttpServletRequest; // Correcto (Spring Boot 3+)

public class WebUtils {
    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        // A veces X-FORWARDED-FOR trae una lista de IPs separadas por coma
        if (remoteAddr != null && remoteAddr.contains(",")) {
            remoteAddr = remoteAddr.split(",")[0].trim();
        }
        return remoteAddr;
    }
}