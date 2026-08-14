package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.TitularFamiliarDao;
import mintur.serviciomedico.model.TitularFamiliar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class TitularFamiliarDaoImpl implements TitularFamiliarDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TitularFamiliarDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TitularFamiliar> familiarMapper = (rs, rowNum) -> {
        TitularFamiliar f = new TitularFamiliar();
        f.setUuidTitularFamiliar(rs.getString("uuid_titular_familiar"));
        f.setUuidTitular(rs.getString("uuid_titular"));
        f.setUuidParentesco(rs.getString("uuid_parentesco"));
        f.setUuidSexo(rs.getString("uuid_sexo"));
        f.setCedulaFamiliar(rs.getInt("cedula_familiar"));
        f.setNombresFamiliar(rs.getString("nombres_familiar"));
        f.setApellidosFamiliar(rs.getString("apellidos_familiar"));
        
        if (rs.getDate("fecha_nac_familiar") != null) {
            f.setFechaNacFamiliar(rs.getDate("fecha_nac_familiar").toLocalDate());
        }
        
        // CORREGIDO: Tenías "numero_history", debe ser "numero_historia" según tu DDL
        f.setNumeroHistoria(rs.getString("numero_historia")); 
        f.setCedulaTitular(rs.getInt("cedula_titular"));
        f.setStatus(rs.getBoolean("status"));

        f.setParentescoDescripcion(rs.getString("parentesco_desc"));
        f.setSexoDescripcion(rs.getString("sexo_desc"));

        return f;
    };

    @Override
    public List<TitularFamiliar> listarPorUuidTitular(String uuidTitular) throws SQLException {
        String sql = """
            SELECT tf.*, p.descripcion AS parentesco_desc, s.descripcion AS sexo_desc
            FROM titular_familiar tf
            LEFT JOIN maestro p ON p.uuid = tf.uuid_parentesco
            LEFT JOIN maestro s ON s.uuid = tf.uuid_sexo
            WHERE tf.uuid_titular = ?::uuid
            ORDER BY tf.cedula_familiar ASC
        """;
        return jdbcTemplate.query(sql, familiarMapper, UUID.fromString(uuidTitular));
    }

    @Override
    public TitularFamiliar buscarPorUUID(String uuid) throws SQLException {
        String sql = """
            SELECT tf.*, p.descripcion AS parentesco_desc, s.descripcion AS sexo_desc
            FROM titular_familiar tf
            LEFT JOIN maestro p ON p.uuid = tf.uuid_parentesco
            LEFT JOIN maestro s ON s.uuid = tf.uuid_sexo
            WHERE tf.uuid_titular_familiar = ?::uuid
        """;
        try {
            return jdbcTemplate.queryForObject(sql, familiarMapper, UUID.fromString(uuid));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public TitularFamiliar buscarPorCedulaFamiliar(int cedula) throws SQLException {
        String sql = """
            SELECT tf.*, p.descripcion AS parentesco_desc, s.descripcion AS sexo_desc
            FROM titular_familiar tf
            LEFT JOIN maestro p ON p.uuid = tf.uuid_parentesco
            LEFT JOIN maestro s ON s.uuid = tf.uuid_sexo
            WHERE tf.cedula_familiar = ?
        """;
        try {
            return jdbcTemplate.queryForObject(sql, familiarMapper, cedula);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void insertar(TitularFamiliar f) throws SQLException {
        String sql = """
            INSERT INTO titular_familiar (
                uuid_titular_familiar, uuid_titular, uuid_parentesco, uuid_sexo, 
                cedula_familiar, nombres_familiar, apellidos_familiar, 
                fecha_nac_familiar, numero_historia, cedula_titular, status
            ) VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
            f.getUuidTitularFamiliar() != null ? UUID.fromString(f.getUuidTitularFamiliar()) : UUID.randomUUID(),
            f.getUuidTitular() != null ? UUID.fromString(f.getUuidTitular()) : null,
            f.getUuidParentesco() != null ? UUID.fromString(f.getUuidParentesco()) : null,
            f.getUuidSexo() != null ? UUID.fromString(f.getUuidSexo()) : null,
            f.getCedulaFamiliar(),
            f.getNombresFamiliar(),
            f.getApellidosFamiliar(),
            f.getFechaNacFamiliar() != null ? Date.valueOf(f.getFechaNacFamiliar()) : null,
            f.getNumeroHistoria(),
            f.getCedulaTitular(),
            f.isStatus()
        );
    }

    @Override
    public void actualizar(TitularFamiliar f) throws Exception {
        String sql = """
            UPDATE titular_familiar SET 
                uuid_parentesco = ?::uuid, 
                uuid_sexo = ?::uuid, 
                cedula_familiar = ?, 
                nombres_familiar = ?, 
                apellidos_familiar = ?, 
                fecha_nac_familiar = ?, 
                numero_historia = ?, 
                status = ?
            WHERE uuid_titular_familiar = ?::uuid
        """;
        jdbcTemplate.update(sql,
            f.getUuidParentesco() != null ? UUID.fromString(f.getUuidParentesco()) : null,
            f.getUuidSexo() != null ? UUID.fromString(f.getUuidSexo()) : null,
            f.getCedulaFamiliar(),
            f.getNombresFamiliar(),
            f.getApellidosFamiliar(),
            f.getFechaNacFamiliar() != null ? Date.valueOf(f.getFechaNacFamiliar()) : null,
            f.getNumeroHistoria(),
            f.isStatus(),
            UUID.fromString(f.getUuidTitularFamiliar())
        );
    }
}