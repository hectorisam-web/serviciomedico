package mintur.serviciomedico.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import mintur.serviciomedico.dao.PresentacionDao;
import mintur.serviciomedico.model.Presentacion;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository // Permite que Spring lo detecte para inyección de dependencias
public class PresentacionDaoImpl implements PresentacionDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired // Inyectamos el JdbcTemplate gestionado por Spring
    public PresentacionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Mapeador reutilizable para convertir filas de la tabla presentaciones en objetos Presentacion.
     */
    private final RowMapper<Presentacion> rowMapper = (rs, rowNum) -> {
        Presentacion p = new Presentacion();
        p.setUuidPresentacion((UUID) rs.getObject("uuid_presentacion"));
        p.setDescripcionPresentacion(rs.getString("descripcion_presentacion"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    };

    @Override
    public void insertar(Presentacion p) throws SQLException {
        String sql = "INSERT INTO presentaciones (descripcion_presentacion, activo) VALUES (?, ?)";
        jdbcTemplate.update(sql, 
            p.getDescripcionPresentacion(), 
            p.isActivo()
        );
    }

    @Override
    public void actualizar(Presentacion p) throws SQLException {
        String sql = "UPDATE presentaciones SET descripcion_presentacion=?, activo=? WHERE uuid_presentacion=?";
        jdbcTemplate.update(sql, 
            p.getDescripcionPresentacion(), 
            p.isActivo(), 
            p.getUuidPresentacion()
        );
    }

    @Override
    public void eliminar(UUID id) throws SQLException {
        String sql = "DELETE FROM presentaciones WHERE uuid_presentacion=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Presentacion> listar() throws SQLException {
        String sql = """
            SELECT uuid_presentacion, descripcion_presentacion, activo 
            FROM presentaciones 
            ORDER BY descripcion_presentacion
        """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public boolean existeDescripcion(String descripcion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM presentaciones WHERE LOWER(descripcion_presentacion) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descripcion);
        return count != null && count > 0;
    }
}