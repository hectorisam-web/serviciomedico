package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.ControlDiarioDao;
import mintur.serviciomedico.model.ControlDiario;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ControlDiarioDaoImpl implements ControlDiarioDao {

    private final JdbcTemplate jdbcTemplate;
    private String uuidConsultorio;

    @Autowired
    public ControlDiarioDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // --- Métodos de Listado (Sin cambios, funcionan correctamente) ---
    @Override
    public List<ControlDiario> listar() throws SQLException {
        String sql = getBaseSelectSQL();
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapControlDiarioFull(rs));
    }

    @Override
    public List<ControlDiario> listarPorTitular(String uuidTitular) throws SQLException {
        String sql = getBaseSelectSQL() + " WHERE cd.uuid_titular = ?::uuid ORDER BY cd.secuencia ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapControlDiarioFull(rs), uuidTitular);
    }

    @Override
    public ControlDiario buscarPorUUID(String uuid) throws SQLException {
        String sql = getBaseSelectSQL() + " WHERE cd.uuid_control = ?::uuid";
        List<ControlDiario> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapControlDiarioFull(rs), uuid);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    // --- INSERT ACTUALIZADO Y ROBUSTO ---
    @Override
    public void insertar(ControlDiario c) throws SQLException {
        String sql = """
            INSERT INTO control_diario
            (uuid_control, uuid_titular, uuid_titular_fam, fecha_control, hora_control,
             peso_kg, tension_sistolica, tension_diastolica, uuid_medico, consultorio, 
             talla_cm, temperatura, motivo_consulta, diagnostico, indicaciones, 
             observaciones, usuario_registro, status)
            VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?::uuid, ?)
        """;

        // Mapeo seguro de UUIDs
        UUID uControl = (c.getUuidControl() == null || c.getUuidControl().isBlank()) ? UUID.randomUUID() : UUID.fromString(c.getUuidControl());
        UUID uTitular = (c.getUuidTitular() != null) ? UUID.fromString(c.getUuidTitular()) : null;
        UUID uFam = (c.getUuidTitularFam() != null && !c.getUuidTitularFam().isBlank()) ? UUID.fromString(c.getUuidTitularFam()) : null;
        UUID uCons = (c.getUuidConsultorio() != null && !c.getUuidConsultorio().isBlank()) ? UUID.fromString(c.getUuidConsultorio()) : null;
        UUID uMedico = (c.getUuidMedico() != null) ? UUID.fromString(c.getUuidMedico()) : null;

        jdbcTemplate.update(sql, 
            uControl,
            uTitular,
            uFam,
            Date.valueOf(c.getFechaControl() != null ? c.getFechaControl() : LocalDate.now()),
            Time.valueOf(c.getHoraControl() != null ? c.getHoraControl() : LocalTime.now()),
            c.getPesoKg(),
            c.getTensionSistolica(),
            c.getTensionDiastolica(),
            uMedico,
            uCons,
            c.getTallaCm(),
            c.getTemperatura(),
            c.getMotivoConsulta(),
            c.getDiagnostico(),
            c.getIndicaciones(),
            c.getObservaciones(),
            c.getUsuarioRegistro(),
            c.isStatus()
        );
    }

    // --- Mapeo y Helpers ---
    private String getBaseSelectSQL() {
        return """
            SELECT 
                            cd.uuid_control,
                            cd.secuencia,
                            cd.uuid_titular,
                            cd.uuid_titular_fam,
                            cd.fecha_control,
                            cd.hora_control,
                            cd.peso_kg,
                            cd.tension_sistolica,
                            cd.tension_diastolica,
                            cd.talla_cm,
                            cd.temperatura,
                            cd.uuid_medico,
                            cd.consultorio,
                            cd.motivo_consulta,
                            cd.diagnostico,
                            cd.indicaciones,
                            cd.observaciones,
                            cd.usuario_registro,
                            cd.status,
                            CASE 
                                WHEN cd.uuid_titular_fam IS NOT NULL THEN TRIM(COALESCE(tf.nombres_familiar, '') || ' ' || COALESCE(tf.apellidos_familiar, ''))
                                ELSE TRIM(COALESCE(t.nombre_titular, '') || ' ' || COALESCE(t.apellidos_titular, ''))
                            END AS "paciente",
                            TRIM(COALESCE(m.nombres_medico, '') || ' ' || COALESCE(m.apellidos_medico, '')) AS "nombreMedico"
                        FROM public.control_diario cd
                        LEFT JOIN public.titular t ON cd.uuid_titular = t.uuid_titular
                        LEFT JOIN public.titular_familiar tf ON cd.uuid_titular_fam = tf.uuid_titular_familiar
                        INNER JOIN public.medicos m ON cd.uuid_medico = m.uuid_medico
        """;
    }

private ControlDiario mapControlDiarioFull(ResultSet rs) throws SQLException {
        ControlDiario c = new ControlDiario();
        c.setSecuencia(rs.getLong("secuencia"));
        c.setUuidControl(rs.getString("uuid_control"));
        c.setUuidTitular(rs.getString("uuid_titular"));
        c.setUuidTitularFam(rs.getString("uuid_titular_fam"));
        
        if (rs.getDate("fecha_control") != null) c.setFechaControl(rs.getDate("fecha_control").toLocalDate());
        if (rs.getTime("hora_control") != null) c.setHoraControl(rs.getTime("hora_control").toLocalTime());
        
        c.setPesoKg(rs.getBigDecimal("peso_kg"));
        c.setTensionSistolica(rs.getObject("tension_sistolica", Integer.class));
        c.setTensionDiastolica(rs.getObject("tension_diastolica", Integer.class));
        c.setTallaCm(rs.getBigDecimal("talla_cm"));
        c.setTemperatura(rs.getBigDecimal("temperatura"));
        c.setUuidMedico(rs.getString("uuid_medico"));
        c.setUuidConsultorio(rs.getString("consultorio"));
        c.setMotivoConsulta(rs.getString("motivo_consulta"));
        c.setDiagnostico(rs.getString("diagnostico"));
        c.setIndicaciones(rs.getString("indicaciones"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setStatus(rs.getBoolean("status"));
        
        // Mapeo limpio usando exactamente los alias definidos en el SQL
        c.setPaciente(rs.getString("paciente"));
        c.setNombreMedico(rs.getString("nombreMedico"));

        String userReg = rs.getString("usuario_registro");
        if (userReg != null) c.setUsuarioRegistro(UUID.fromString(userReg));
        
        return c;
    }

    // Getters/Setters omitidos para brevedad
    @Override public String getUuidConsultorio() { return uuidConsultorio; }
    @Override public void setUuidConsultorio(String uuidConsultorio) { this.uuidConsultorio = uuidConsultorio; }
    
    @Override
    public void actualizarSignosVitales(ControlDiario c) throws SQLException {
        String sql = """
            UPDATE public.control_diario
            SET 
                motivo_consulta = ?,
                diagnostico = ?,
                indicaciones = ?,
                observaciones = ?,
                peso_kg = ?,
                tension_sistolica = ?,
                tension_diastolica = ?,
                talla_cm = ?,
                temperatura = ?
            WHERE uuid_control = ?::uuid
        """;

        jdbcTemplate.update(sql,
            c.getMotivoConsulta(),
            c.getDiagnostico(),
            c.getIndicaciones(),
            c.getObservaciones(),
            c.getPesoKg(),
            c.getTensionSistolica(),
            c.getTensionDiastolica(),
            c.getTallaCm(),
            c.getTemperatura(),
            c.getUuidControl()
        );
    }
    
    @Override
    public List<ControlDiario> listarPorFecha(LocalDate fecha) throws SQLException {
        String sql = getBaseSelectSQL() + " WHERE cd.fecha_control = ? ORDER BY cd.secuencia";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapControlDiarioFull(rs), Date.valueOf(fecha));
    }
}