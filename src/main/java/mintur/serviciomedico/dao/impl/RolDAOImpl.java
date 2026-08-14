package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.RolDAO;
import mintur.serviciomedico.model.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RolDAOImpl implements RolDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RolDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Rol> rolMapper = (rs, rowNum) -> {
        Rol r = new Rol();
        r.setUuidRol(rs.getString("uuid_rol"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setEstatus(rs.getBoolean("estatus"));
        return r;
    };

    @Override
    public List<Rol> listarActivos() throws Exception {
        String sql = "SELECT uuid_rol, descripcion, estatus FROM roles WHERE estatus = true ORDER BY descripcion";
        return jdbcTemplate.query(sql, rolMapper);
    }

    @Override
    public List<Rol> listarTodos() throws Exception {
        String sql = "SELECT uuid_rol, descripcion, estatus FROM roles ORDER BY id_codigo,descripcion";
        return jdbcTemplate.query(sql, rolMapper);
    }

    @Override
    public Set<String> obtenerPermisos(String uuidRol) throws Exception {
        String sql = """
            SELECT p.modulo
            FROM roles_permisos rp
            JOIN permisos p ON p.uuid_permiso = rp.uuid_permiso
            WHERE rp.uuid_rol = ?
        """;
        List<String> lista = jdbcTemplate.queryForList(sql, String.class, UUID.fromString(uuidRol));
        return new HashSet<>(lista);
    }
    
    @Override
    public List<String> listarModulosSistema() throws Exception {
        String sql = "SELECT modulo FROM permisos ORDER BY modulo";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    @Transactional
    public void guardarPermisos(String uuidRol, Set<String> permisos) throws Exception {
        String deleteSQL = "DELETE FROM roles_permisos WHERE uuid_rol = ?";
        jdbcTemplate.update(deleteSQL, UUID.fromString(uuidRol));

        if (permisos != null && !permisos.isEmpty()) {
            String insertSQL = """
                INSERT INTO roles_permisos (uuid_rol, uuid_permiso)
                SELECT ?, uuid_permiso FROM permisos WHERE modulo = ?
            """;

            List<String> modulos = new ArrayList<>(permisos);
            jdbcTemplate.batchUpdate(insertSQL, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setObject(1, UUID.fromString(uuidRol));
                    ps.setString(2, modulos.get(i));
                }

                @Override
                public int getBatchSize() {
                    return modulos.size();
                }
            });
        }
    }

    @Override
    public void insertar(Rol rol) throws Exception {
        String sql = "INSERT INTO roles (descripcion, estatus) VALUES (?, ?) RETURNING uuid_rol";
        String nuevoUuid = jdbcTemplate.queryForObject(sql, String.class, rol.getDescripcion(), rol.isEstatus());
        rol.setUuidRol(nuevoUuid); 
    }

    @Override
    public void actualizar(Rol rol) throws Exception {
        String sql = "UPDATE roles SET descripcion = ?, estatus = ? WHERE uuid_rol = ?";
        jdbcTemplate.update(sql, 
            rol.getDescripcion(), 
            rol.isEstatus(), 
            UUID.fromString(rol.getUuidRol())
        );
    }
}