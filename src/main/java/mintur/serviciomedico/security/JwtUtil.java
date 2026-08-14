package mintur.serviciomedico.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    // La clave debe ser larga para cumplir con la seguridad de HS256
    private final String SECRET_STR = "M1ntur_Secret_Key_2026_Secure_Vector_Long_String_For_HS256"; 
    private final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STR.getBytes(StandardCharsets.UTF_8));

    public String generarToken(String usuarioId, String nombre) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("nombre", nombre);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuarioId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 Horas
                .signWith(KEY, SignatureAlgorithm.HS256) // Usamos la Key generada
                .compact();
    }

    public String extraerUsuarioId(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        // En 0.11+ se usa parserBuilder()
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public Boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}