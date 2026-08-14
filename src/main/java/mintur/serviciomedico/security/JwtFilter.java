package mintur.serviciomedico.security;

import mintur.serviciomedico.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Lazy
    private JwtUtil jwtUtil;

    @Autowired
    @Lazy
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Configurar CORS (necesario para todas las peticiones)
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 2. Exclusión de rutas públicas: Si es login o pública, salimos de la validación JWT
        if (path.contains("/auth/") || path.contains("/api/auth/")) {
            filterChain.doFilter(request, response);
        return;
        }

        // 3. Extracción y validación de Token
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            if (jwtUtil.validarToken(token)) {
                String usuarioId = jwtUtil.extraerUsuarioId(token);
                
                // Carga de permisos
                List<String> permisos = usuarioService.listarPermisosAsignados(usuarioId);
                List<SimpleGrantedAuthority> authorities = permisos.stream()
                        .filter(p -> p != null && !p.trim().isEmpty())
                        .map(p -> new SimpleGrantedAuthority(p.trim()))
                        .collect(Collectors.toList());
                
                // --- AGREGA ESTA LÍNEA PARA VER EL CONTENIDO ---
                System.out.println(">>> [DEBUG] Autoridades inyectadas al contexto: " + authorities);
                
                UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(usuarioId, null, authorities);
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println(">>> [JwtFilter] Usuario autenticado y contexto poblado: " + usuarioId);
            } else {
                System.out.println(">>> [JwtFilter] Token inválido.");
            }
        } catch (Exception e) {
            System.out.println(">>> [JwtFilter] Error procesando JWT: " + e.getMessage());
        }

        // 4. Continuar cadena
        filterChain.doFilter(request, response);
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}