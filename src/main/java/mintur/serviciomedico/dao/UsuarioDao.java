package mintur.serviciomedico.dao;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import mintur.serviciomedico.model.Usuario;
import mintur.serviciomedico.model.Maestro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import org.springframework.dao.DataAccessException;

@Repository 
public class UsuarioDao {
    private static final Logger logger = Logger.getLogger(UsuarioDao.class.getName());
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    
    
    @Autowired 
    public UsuarioDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    // --- AUTENTICACIÓN (LO QUE YA FUNCIONABA) ---
    public Usuario buscarPorUsuario(String usuario) throws Exception {
        String sql = """
            SELECT u.uuid_usuario, u.usuario, u.pass, u.nombres, u.apellidos,
                   u.uuid_rol, r.descripcion AS nombre_rol, u.estatus
            FROM public.usuarios u
            LEFT JOIN public.roles r ON u.uuid_rol = r.uuid_rol
            WHERE u.usuario = ? AND u.estatus = true
            """;
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // --- CRUD ADMINISTRATIVO (NUEVO) ---
public List<Usuario> findAll() throws Exception {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT u.*, r.descripcion as nombre_rol FROM public.usuarios u " +
                 "LEFT JOIN public.roles r ON u.uuid_rol = r.uuid_rol ORDER BY u.apellidos";
    
    try (Connection cn = dataSource.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            try {
                Usuario u = mapRow(rs);
                lista.add(u);
            } catch (Exception e) {
                System.err.println("!!! ERROR al mapear la fila: " + rs.getString("usuario"));
                e.printStackTrace(); // Esto nos dirá si es un problema de tipos (ej: fecha nula)
            }
        }
        System.out.println("DEBUG FINAL: Usuarios encontrados en BD: " + lista.size());
    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
    return lista;
}

    public void insertar(Usuario u) throws Exception {
        String sql = "INSERT INTO public.usuarios (usuario, pass, nombres, apellidos, uuid_rol, estatus) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPass()); 
            ps.setString(3, u.getNombres());
            ps.setString(4, u.getApellidos());
            ps.setObject(5, UUID.fromString(u.getUuidRol()));
            ps.setBoolean(6, u.isEstatus());
            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario u) throws Exception {
        String sql = "UPDATE public.usuarios SET nombres = ?, apellidos = ?, uuid_rol = ?, estatus = ? WHERE uuid_usuario = ?";
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombres());
            ps.setString(2, u.getApellidos());
            ps.setObject(3, UUID.fromString(u.getUuidRol()));
            ps.setBoolean(4, u.isEstatus());
            ps.setObject(5, UUID.fromString(u.getUuidUsuario()));
            ps.executeUpdate();
        }
    }
    
    public void actualizarRol(String uuidUsuario, String uuidRol) throws Exception {
    String sql = "UPDATE public.usuarios SET uuid_rol = ? WHERE uuid_usuario = ?";
    try (Connection cn = dataSource.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setObject(1, UUID.fromString(uuidRol));
        ps.setObject(2, UUID.fromString(uuidUsuario));
        ps.executeUpdate();
    }
}

  public List<String> findPermisosByUsuarioId(String uuidUsuario) {
    // IMPORTANTE: Seleccionamos 'p.descripcion' (o el nombre de tu columna de nombre)
    // en lugar de 'rp.uuid_permiso'.
   String sql = "SELECT DISTINCT m.descripcion " +
                 "FROM roles_permisos rp " +
                 "JOIN usuarios u ON rp.uuid_rol = u.uuid_rol " +
                 "JOIN maestro m ON rp.uuid_permiso = m.uuid " + 
                 "WHERE u.uuid_usuario = ?::uuid";
    
    return jdbcTemplate.queryForList(sql, String.class, uuidUsuario);
} 
  
// Obtener todos los permisos del maestro (para el checkbox)
public List<Map<String, String>> findAllPermisos() {
    // Traemos TODOS los permisos de la tabla maestro (padres e hijos)
    // Filtramos maestro != '0' para excluir registros raíz si es necesario
    String sql = "SELECT uuid::text, descripcion, id_codigo, maestro FROM public.maestro WHERE maestro != '0'"; 
    
    return jdbcTemplate.queryForList(sql).stream().map(row -> {
        Map<String, String> m = new java.util.HashMap<>();
        
        m.put("uuid", String.valueOf(row.get("uuid")));
        m.put("descripcion", String.valueOf(row.get("descripcion")));
        
        // El id_codigo para ordenar
        Object idCodigo = row.get("id_codigo");
        m.put("id_codigo", idCodigo != null ? String.valueOf(idCodigo) : "0");
        
        // El campo 'maestro' es la llave foránea que identifica al padre
        Object maestro = row.get("maestro");
        m.put("maestro", maestro != null ? String.valueOf(maestro) : "0");
        
        return m;
    }).collect(Collectors.toList());
}
    
    // 1. Método para obtener el UUID del rol asociado al usuario
    public String obtenerUuidRolPorUsuario(String uuidUsuario) {
    try {
        String sql = "SELECT uuid_rol::text FROM public.usuarios WHERE uuid_usuario = ?::uuid";
        return jdbcTemplate.queryForObject(sql, String.class, uuidUsuario);
    } catch (Exception e) {
        logger.severe("Error buscando rol para usuario " + uuidUsuario + ": " + e.getMessage());
        return null; // O lanza una excepción propia
    }
}

    // 2. Método para eliminar permisos existentes antes de guardar los nuevos
    public void eliminarPermisosDelRol(String uuidRol) {
        String sql = "DELETE FROM public.roles_permisos WHERE uuid_rol = ?::uuid";
        jdbcTemplate.update(sql, uuidRol);
    }

    // 3. Método para insertar cada nuevo permiso
    public void insertarPermisoEnRol(String uuidRol, String uuidPermiso) {
        String sql = "INSERT INTO public.roles_permisos (uuid_rol, uuid_permiso) VALUES (?::uuid, ?::uuid)";
        jdbcTemplate.update(sql, uuidRol, uuidPermiso);
    }


    // --- MAPPER UNIFICADO ---
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setUuidUsuario(rs.getString("uuid_usuario"));
        u.setUsuario(rs.getString("usuario"));
        u.setPass(rs.getString("pass"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setUuidRol(rs.getString("uuid_rol"));
        u.setDescripcionRol(rs.getString("nombre_rol"));
        u.setEstatus(rs.getBoolean("estatus"));
        return u;
    }
}