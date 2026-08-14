package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.RolPermisoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

@Repository
public class RolPermisoDaoImpl implements RolPermisoDao {

    private final JdbcTemplate jdbcTemplate;

    public RolPermisoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<String> listarPermisosPorRol(String uuidRol) {
        String sql = "SELECT uuid_permiso FROM roles_permisos WHERE uuid_rol = ?";
        return jdbcTemplate.queryForList(sql, String.class, UUID.fromString(uuidRol));
    }

    @Override
    public void eliminarPermisos(String uuidRol) {
        String sql = "DELETE FROM roles_permisos WHERE uuid_rol = ?";
        int rows = jdbcTemplate.update(sql, UUID.fromString(uuidRol));
        System.out.println(">>> [DAO] Permisos eliminados para rol " + uuidRol + ": " + rows);
    }

    @Override
    public void insertarPermisos(String uuidRol, List<String> permisos) {
        String sql = "INSERT INTO roles_permisos(uuid_rol, uuid_permiso) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, UUID.fromString(uuidRol));
                ps.setObject(2, UUID.fromString(permisos.get(i)));
            }
            @Override
            public int getBatchSize() {
                return permisos.size();
            }
        });
        System.out.println(">>> [DAO] Permisos insertados para rol " + uuidRol + ": " + permisos.size());
    }
    
    @Override
    public List<Map<String, String>> listarTodosLosPermisos() {
    String sql = "SELECT uuid, id_codigo, descripcion FROM maestro WHERE id_codigo BETWEEN 900 AND 999 ORDER BY id_codigo ASC";
    return jdbcTemplate.query(sql, (rs, rowNum) -> {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("id", rs.getString("uuid"));
        map.put("id_codigo", rs.getString("id_codigo")); // Opcional, por si lo necesitas en el frontend
        map.put("descripcion", rs.getString("descripcion"));
        return map;
    });
}
       
}