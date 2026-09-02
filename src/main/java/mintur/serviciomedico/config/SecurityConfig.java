package mintur.serviciomedico.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mintur.serviciomedico.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            
            // 1. Permiso total y absoluto para el login y rutas de autenticación (con y sin prefijo de proxy)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/**", "/api/auth/**").permitAll()
            .requestMatchers("/auth/**", "/api/auth/**").permitAll()
            //.requestMatchers("/api/control-medicamento/**").permitAll()
                
            // Demás rutas públicas necesarias
            .requestMatchers("/api/usuarios/**", "/usuarios/**").permitAll()
            .requestMatchers("/api/reportes/**").permitAll()

            // 2. Maestros y resto de reglas...
            .requestMatchers("/api/maestros/**").hasAnyAuthority(
                "Administracion", "Control Diario Pacientes", "Titular/Familiares", "Personal Médico", "Maestro","Administrador de Sistemas",
                "Administrador de Servico Medico", "Farmacia", "Citas"
            )
            
            // 3. Titulares, Medicos, Medicamentos
            .requestMatchers("/api/titulares/**").hasAnyAuthority("Titular/Familiares", "Control Diario Pacientes", "Administracion")
            .requestMatchers("/api/medicos/**").hasAnyAuthority("Personal Médico", "Administracion", "Control Diario Pacientes", "Administrador de Sistemas","Administrador de Servico Medico", "Citas")
                
            /* 3.1 Medicamentos y Catálogo (Estas son las que controlarán el acceso correctamente)
            .requestMatchers(HttpMethod.GET, "/api/control-medicamento/**", "/api/control-medicamentos/**").hasAnyAuthority(
            "Entrega Medicamentos", "Control Diario", "Farmacia", "Administracion", "Administrador de Sistemas", "Personal Médico"
            )*/
             // Permite las consultas GET del control de medicamentos sin exigir una autoridad estricta temporalmente
            .requestMatchers(HttpMethod.GET, "/api/control-medicamento/**").permitAll()   
              
            .requestMatchers(HttpMethod.POST, "/api/control-medicamento/**", "/api/control-medicamentos/**").hasAnyAuthority(
            "Entrega Medicamentos", "Control Diario", "Farmacia", "Administracion", "Administrador de Sistemas", "Personal Médico"
            )
            .requestMatchers(HttpMethod.PUT, "/api/control-medicamento/**", "/api/control-medicamentos/**").hasAnyAuthority(
            "Entrega Medicamentos", "Control Diario", "Farmacia", "Administracion", "Administrador de Sistemas", "Personal Médico"
            )
                
            // 4. Inventarios / Reportes (Pon las rutas específicas PRIMERO)
            .requestMatchers("/api/control-diario/hoy").hasAnyAuthority("Control Diario", "Seguimiento", "Administracion", "Farmacia", "Personal Médico", "Personal Medico")
            .requestMatchers("/api/control-diario/**").hasAnyAuthority("Control Diario", "Administracion", "MEDICO", "Farmacia")
            .requestMatchers("/api/control-medicamentos/**").hasAnyAuthority("Control Diario", "Seguimiento", "Administracion", "Farmacia", "Personal Médico", "Personal Medico")
             
            // 4.1 Control de Citas 
            .requestMatchers(HttpMethod.GET, "/api/citas/**").hasAnyAuthority("Control Diario",  "Administracion", "Administrador de Sistemas",  "Personal Médico", "Farmacia")
            .requestMatchers(HttpMethod.POST, "/api/citas/**").hasAnyAuthority("Control Diario", "Administracion", "Administrador de Sistemas",  "Personal Médico", "Farmacia")
            .requestMatchers(HttpMethod.PUT, "/api/citas/**").hasAnyAuthority("Control Diario",  "Administracion", "Administrador de Sistemas" , "Personal Médico", "Farmacia")  
                
             // 4.2 Reportes Generales y Módulos
            .requestMatchers("/api/reportes/**").hasAnyAuthority(
                "Administracion", "Control Diario", "Farmacia", "Inventario", "Administrador de Sistemas", "Personal Médico", "Personal Medico"
            )
            .requestMatchers("/api/inventario/**").hasAnyAuthority(
                "Administracion", "Inventario", "Administrador de Sistemas"
            )
            .requestMatchers("/api/farmacia/**").hasAnyAuthority(
                "Administracion", "Farmacia", "Seguimiento", "Administrador de Sistemas", "Personal Médico"
            )
                
            // 5. Roles
            .requestMatchers("/api/roles/guardar").hasAuthority("Administracion")
            .requestMatchers("/api/roles/listar", "/api/roles/activos" ).hasAnyAuthority("Administracion", "Administrador de Sistemas", "Administrador de Servico Medico", "Roles", "Usuarios")
            .requestMatchers("/api/roles/*/permisos").hasAnyAuthority("Administracion", "Administrador de Sistemas", "Administrador de Servico Medico", "Roles")

            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> {
            System.err.println(">>> ACCESO DENEGADO EN RUTA: " + req.getRequestURI() + " | Causa: " + e.getMessage());
            res.sendError(403, "Acceso denegado: " + e.getMessage());
        }))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://centromedico.mintur.gob.ve"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "X-Operador-ID"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Filtro integrado para registrar cada petición en la consola del servidor y auditar el tráfico
    @Component
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public static class RequestLoggingFilter implements Filter {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            System.out.println(">>> ENTRANTE: " + req.getMethod() + " " + req.getRequestURI() + " | Origin: " + req.getHeader("Origin"));
            chain.doFilter(request, response);
        }
    }
}