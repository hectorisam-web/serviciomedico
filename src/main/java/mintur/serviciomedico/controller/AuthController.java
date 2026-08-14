package mintur.serviciomedico.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import mintur.serviciomedico.model.Usuario;
import mintur.serviciomedico.service.UsuarioService;
import mintur.serviciomedico.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://centromedico.mintur.gob.ve"}, allowCredentials = "true")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println(">>> CONTROLLER LOGIN - Usuario recibido: " + request.getUsuario());
            Map<String, Object> resultado = usuarioService.loginConPermisos(request.getUsuario(), request.getPassword());

            if (resultado == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciales inválidas"));
            }

            Usuario usuario = (Usuario) resultado.get("usuario");
            @SuppressWarnings("unchecked")
            List<String> listaPermisos = (List<String>) resultado.get("permisos");

            String token = jwtUtil.generarToken(
                usuario.getUuidUsuario().toString(), 
                usuario.getNombres()
            );

            // Se usa uuid_usuario para mantener consistencia con la tabla public.usuarios
            UsuarioResponse response = new UsuarioResponse(
                    usuario.getUuidUsuario().toString(), 
                    usuario.getUsuario(),
                    usuario.getNombres(),
                    usuario.getDescripcionRol(),
                    token,
                    listaPermisos
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.severe("Error en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno del servidor"));
        }
    }
    
    public static class LoginRequest {
        private String usuario;
        private String password;
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
 public static class UsuarioResponse {
        @JsonProperty("uuid_usuario") private String uuid_usuario; // Nombre exacto
        @JsonProperty("usuario") private String usuario;
        @JsonProperty("nombre") private String nombre;
        @JsonProperty("rol") private String rol;
        @JsonProperty("token") private String token;
        @JsonProperty("permisos") private List<String> permisos;

        public UsuarioResponse(String uuid_usuario, String usuario, String nombre, String rol, String token, List<String> permisos) {
            this.uuid_usuario = uuid_usuario;
            this.usuario = usuario;
            this.nombre = nombre;
            this.rol = rol;
            this.token = token;
            this.permisos = permisos;
        }

        // Getters ajustados al nombre del campo
        public String getUuid_usuario() { return uuid_usuario; }
        public String getUsuario() { return usuario; }
        public String getNombre() { return nombre; }
        public String getRol() { return rol; }
        public String getToken() { return token; }
        public List<String> getPermisos() { return permisos; }
    }

    public static class ErrorResponse {
        private String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
}