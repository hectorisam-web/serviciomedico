package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.TitularDao;
import mintur.serviciomedico.model.Titular;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class TitularDaoImpl implements TitularDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TitularDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Titular> titularMapper = (rs, rowNum) -> {
        Titular t = new Titular();
        t.setUuidTitular(rs.getString("uuid_titular"));
        t.setCedulaTitular(rs.getInt("cedula_titular"));
        t.setApellidosTitular(rs.getString("apellidos_titular"));
        t.setNombreTitular(rs.getString("nombre_titular"));
        
        t.setFechaNacimiento(rs.getDate("fecha_nacimiento") != null 
            ? rs.getDate("fecha_nacimiento").toLocalDate() 
            : null);
        
        t.setUuidOrganismo(rs.getString("uuid_organismo"));
        t.setUuidSexo(rs.getString("uuid_sexo"));
        t.setNroHistoria(rs.getString("nro_historia"));
        t.setStatus(rs.getBoolean("status"));
        t.setTelefono(rs.getString("telefono"));

        // Campos de descripción traídos por el JOIN
        t.setNombreOrganismo(rs.getString("nombre_organismo"));
        t.setNombreSexo(rs.getString("nombre_sexo"));
        
        return t;
    };

    @Override
    public Titular buscarPorCedula(Integer cedula) throws SQLException {
        // Corregido: SQL estándar para JdbcTemplate con "?" y eliminación de "ilike" incorrecto para un Integer
        String sql = """
            SELECT t.*, o.descripcion AS nombre_organismo, s.descripcion AS nombre_sexo 
            FROM titular t 
            LEFT JOIN maestro o ON t.uuid_organismo = o.uuid 
            LEFT JOIN maestro s ON t.uuid_sexo = s.uuid 
            WHERE t.cedula_titular = ?
        """;
        try {
            return jdbcTemplate.queryForObject(sql, titularMapper, cedula);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Titular buscarPorUuid(String uuid) throws SQLException {
        // Corregido: Se agregaron los JOINs para no perder los nombres al buscar por ID
        String sql = """
            SELECT t.*, o.descripcion AS nombre_organismo, s.descripcion AS nombre_sexo 
            FROM titular t 
            LEFT JOIN maestro o ON t.uuid_organismo = o.uuid 
            LEFT JOIN maestro s ON t.uuid_sexo = s.uuid 
            WHERE t.uuid_titular = ?
        """;
        try {
            return jdbcTemplate.queryForObject(sql, titularMapper, UUID.fromString(uuid));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Titular> obtenerTodos() throws SQLException {
        String sql = """
            SELECT t.*, m_org.descripcion AS nombre_organismo, m_sex.descripcion AS nombre_sexo 
            FROM titular t 
            LEFT JOIN maestro m_org ON t.uuid_organismo = m_org.uuid 
            LEFT JOIN maestro m_sex ON t.uuid_sexo = m_sex.uuid 
            ORDER BY t.apellidos_titular, t.nombre_titular
        """;
        return jdbcTemplate.query(sql, titularMapper);
    }

    @Override
    public List<Object> listarGrupoFamiliar(String uuidTitular) throws SQLException {
        String sql = "SELECT nombre_familiar, apellido_familiar FROM titular_familiar WHERE uuid_titular = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> 
            rs.getString("nombre_familiar") + " " + rs.getString("apellido_familiar"),
            UUID.fromString(uuidTitular)
        );
    }

    @Override
    public void insertar(Titular t) throws SQLException {
        String sql = """
            INSERT INTO titular 
            (uuid_titular, cedula_titular, apellidos_titular, nombre_titular, 
             fecha_nacimiento, uuid_organismo, uuid_sexo, nro_historia, status, telefono)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
            t.getUuidTitular() != null ? UUID.fromString(t.getUuidTitular()) : UUID.randomUUID(),
            t.getCedulaTitular(),
            t.getApellidosTitular(),
            t.getNombreTitular(),
            t.getFechaNacimiento(),
            t.getUuidOrganismo() != null ? UUID.fromString(t.getUuidOrganismo()) : null,
            t.getUuidSexo() != null ? UUID.fromString(t.getUuidSexo()) : null,
            t.getNroHistoria(),
            t.isStatus(),
            t.getTelefono()
        );
    }

    @Override
    public void actualizar(Titular t) throws SQLException {
        String sql = """
            UPDATE titular 
            SET cedula_titular = ?, apellidos_titular = ?, nombre_titular = ?, 
                fecha_nacimiento = ?, uuid_organismo = ?, uuid_sexo = ?, 
                nro_historia = ?, status = ?, telefono = ?
            WHERE uuid_titular = ?
        """;
        jdbcTemplate.update(sql,
            t.getCedulaTitular(),
            t.getApellidosTitular(),
            t.getNombreTitular(),
            t.getFechaNacimiento(),
            t.getUuidOrganismo() != null ? UUID.fromString(t.getUuidOrganismo()) : null,
            t.getUuidSexo() != null ? UUID.fromString(t.getUuidSexo()) : null,
            t.getNroHistoria(),
            t.isStatus(),
            t.getTelefono(),
            UUID.fromString(t.getUuidTitular())
        );
    }

    @Override
    public void eliminar(String uuid) throws SQLException {
        String sql = "UPDATE titular SET status = false WHERE uuid_titular = ?";
        jdbcTemplate.update(sql, UUID.fromString(uuid));
    }
}