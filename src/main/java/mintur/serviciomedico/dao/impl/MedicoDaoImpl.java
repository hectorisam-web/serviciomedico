package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.MedicoDao;
import mintur.serviciomedico.model.Medico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MedicoDaoImpl implements MedicoDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // MAPPER: Mapeo exacto según tu CREATE TABLE y JOIN con Maestro
private final RowMapper<Medico> medicoMapper = (rs, rowNum) -> {
        Medico m = new Medico();
        m.setUuidMedico(rs.getString("uuid_medico"));
        m.setUuidEspecialidad(rs.getString("uuid_especialidad"));
        m.setCedulaMedico(rs.getInt("cedula_medico"));
        m.setNombresMedico(rs.getString("nombres_medico"));
        m.setApellidosMedico(rs.getString("apellidos_medico"));
        m.setTelefonoMedico(rs.getString("telefono_medico")); // Respetado
        m.setEmailMedico(rs.getString("email_medico"));       // Respetado
        m.setStatus(rs.getBoolean("status"));
        m.setUuidConsultorio(rs.getString("uuid_consultorio"));
        m.setMpps(rs.getString("mpps")); 
        m.setDescripcionEspecialidad(rs.getString("especialidad_nombre"));
        return m;
    };

    @Override
    public void insertar(Medico m) throws Exception {
        String sql = """
            INSERT INTO medicos 
            (uuid_medico, uuid_especialidad, cedula_medico, nombres_medico, apellidos_medico,
             telefono_medico, email_medico, status, uuid_consultorio, mpps)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
            UUID.fromString(m.getUuidMedico()),
            parseUuid(m.getUuidEspecialidad()),
            m.getCedulaMedico(),
            m.getNombresMedico(),
            m.getApellidosMedico(),
            m.getTelefonoMedico(),
            m.getEmailMedico(),
            m.isStatus(),
            parseUuid(m.getUuidConsultorio()),
            m.getMpps()
        );
    }

@Override
    public void actualizar(Medico m) throws Exception {
        String sql = """
            UPDATE medicos SET uuid_especialidad = ?, cedula_medico = ?, nombres_medico = ?, 
            apellidos_medico = ?, telefono_medico = ?, email_medico = ?, status = ?, 
            uuid_consultorio = ?, mpps = ? WHERE uuid_medico = ?
        """;
        jdbcTemplate.update(sql,
            parseUuid(m.getUuidEspecialidad()), m.getCedulaMedico(),
            m.getNombresMedico(), m.getApellidosMedico(),
            m.getTelefonoMedico(), m.getEmailMedico(),
            m.isStatus(), parseUuid(m.getUuidConsultorio()),
            m.getMpps(), UUID.fromString(m.getUuidMedico())
        );
    }

    @Override
    public List<Medico> listarTodos() throws SQLException {
        // DISTINCT ON elimina duplicados visuales causados por el JOIN
        String sql = """
            SELECT DISTINCT ON (m.uuid_medico) 
                   m.*, ma.descripcion as especialidad_nombre
            FROM medicos m
            LEFT JOIN maestro ma ON m.uuid_especialidad = ma.uuid
            ORDER BY m.uuid_medico, m.cedula_medico ASC
        """;
        return jdbcTemplate.query(sql, medicoMapper);
    }

    @Override
    public List<Medico> listarActivos() throws SQLException {
        String sql = """
            SELECT DISTINCT ON (m.uuid_medico)
                   m.*, ma.descripcion as especialidad_nombre
            FROM medicos m
            LEFT JOIN maestro ma ON m.uuid_especialidad = ma.uuid
            WHERE m.status = true
            ORDER BY m.uuid_medico, m.cedula_medico ASC
        """;
        return jdbcTemplate.query(sql, medicoMapper);
    }

    @Override
    public Medico buscarPorUuid(String uuid) throws SQLException {
        String sql = """
            SELECT m.*, ma.descripcion as especialidad_nombre
            FROM medicos m
            LEFT JOIN maestro ma ON m.uuid_especialidad = ma.uuid
            WHERE m.uuid_medico = ?
        """;
        List<Medico> resultados = jdbcTemplate.query(sql, medicoMapper, UUID.fromString(uuid));
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // ESTE ES EL MÉTODO QUE FALTABA Y CAUSABA EL ERROR DE COMPILACIÓN
   @Override
    public Medico buscarPorCedula(int cedula) throws Exception {
        String sql = "SELECT m.*, ma.descripcion as especialidad_nombre FROM medicos m " +
                     "LEFT JOIN maestro ma ON m.uuid_especialidad = ma.uuid WHERE m.cedula_medico = ?";
        List<Medico> res = jdbcTemplate.query(sql, medicoMapper, cedula);
        return res.isEmpty() ? null : res.get(0);
    }

    private UUID parseUuid(String s) {
        return (s == null || s.isBlank() || s.equals("null")) ? null : UUID.fromString(s);
    }
    
    @Override
    public boolean tieneConsultas(String uuid) {
        String sql = "SELECT COUNT(*) FROM control_diario WHERE uuid_medico = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, UUID.fromString(uuid));
        return count != null && count > 0;
    }

    @Override
    public void eliminar(String uuid) throws Exception {
        String sql = "DELETE FROM medicos WHERE uuid_medico = ?";
        jdbcTemplate.update(sql, UUID.fromString(uuid));
    }
    /*
    private UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank() || uuidStr.equals("null")) {
            return null;
        }
        return UUID.fromString(uuidStr);
    }
    */
}