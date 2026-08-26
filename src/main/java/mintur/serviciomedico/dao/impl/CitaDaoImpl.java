package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.CitaDao;
import mintur.serviciomedico.model.Cita;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public class CitaDaoImpl implements CitaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Cita> citaRowMapper = new RowMapper<Cita>() {
        @Override
        public Cita mapRow(ResultSet rs, int rowNum) throws SQLException {
            Cita c = new Cita();
            c.setUuidCita((UUID) rs.getObject("uuid_cita"));
            c.setUuidTitular((UUID) rs.getObject("uuid_titular"));
            c.setUuidTitularFamiliar((UUID) rs.getObject("uuid_titular_familiar"));
            c.setUuidConsultorio((UUID) rs.getObject("uuid_consultorio"));
            c.setUuidMedico((UUID) rs.getObject("uuid_medico"));
            
            if (rs.getDate("fecha") != null) c.setFecha(rs.getDate("fecha").toLocalDate());
            if (rs.getDate("fecha_cita") != null) c.setFechaCita(rs.getDate("fecha_cita").toLocalDate());
            if (rs.getTime("hora") != null) c.setHora(rs.getTime("hora").toLocalTime());
            
            c.setObservaciones(rs.getString("observaciones"));
            c.setEstado(rs.getString("estado"));
            
            // Mapeo opcional si tu entidad Cita tiene estos campos para la vista
            try {
                c.setNombreMedico(rs.getString("nombreMedico"));
                c.setNombreConsultorio(rs.getString("nombreConsultorio"));
            } catch (SQLException ignored) {
                // Por si alguna consulta no trae estos alias
            }
            
            return c;
        }
    };

    @Override
    public List<Cita> listarCitas() {
        String sql = "SELECT c.uuid_cita, c.uuid_titular, c.uuid_titular_familiar, c.uuid_consultorio, c.uuid_medico, \n" +
                     "       c.fecha, c.fecha_cita, c.hora, c.observaciones, c.estado,\n" +
                     "       CONCAT(m.nombres_medico, ' ', m.apellidos_medico) AS nombreMedico,\n" +
                     "       con.descripcion AS nombreConsultorio\n" +
                     "FROM citas c\n" +
                     "LEFT JOIN medicos m ON c.uuid_medico = m.uuid_medico\n" +
                     "LEFT JOIN maestro con ON c.uuid_consultorio = con.uuid";
        return jdbcTemplate.query(sql, citaRowMapper);
    }

    @Override
    public boolean guardarCita(Cita cita) {
        try {
            UUID nuevoUuid = UUID.randomUUID();
            cita.setUuidCita(nuevoUuid);
            
            if (cita.getFecha() == null) {
                cita.setFecha(LocalDate.now());
            }
            
            String sql = "INSERT INTO citas (uuid_cita, uuid_titular, uuid_titular_familiar, uuid_consultorio, uuid_medico, fecha, fecha_cita, hora, observaciones, estado) " +
                         "VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?)";
            
            int filas = jdbcTemplate.update(sql,
                nuevoUuid,
                cita.getUuidTitular(),
                cita.getUuidTitularFamiliar(),
                cita.getUuidConsultorio(),
                cita.getUuidMedico(),
                java.sql.Date.valueOf(cita.getFecha()),
                java.sql.Date.valueOf(cita.getFecha()),
                java.sql.Time.valueOf(cita.getHora()),
                cita.getObservaciones() != null ? cita.getObservaciones() : "",
                cita.getEstado() != null ? cita.getEstado() : "PROGRAMADA"
            );

            // Si se creó directamente como ATENDIDA, la pasamos al control diario de inmediato
            if (filas > 0 && "ATENDIDA".equalsIgnoreCase(cita.getEstado())) {
                pasarAControlDiario(cita);
            }

            return filas > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar cita: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarCita(UUID uuidCita, Cita cita) {
        try {
            String sql = "UPDATE citas SET uuid_titular = ?, uuid_titular_familiar = ?, uuid_consultorio = ?, uuid_medico = ?, fecha = ?, fecha_cita = ?, hora = ?, observaciones = ?, estado = ? " +
                         "WHERE uuid_cita = ?::uuid";
            
            int filas = jdbcTemplate.update(sql,
                cita.getUuidTitular(),
                cita.getUuidTitularFamiliar(),
                cita.getUuidConsultorio(),
                cita.getUuidMedico(),
                java.sql.Date.valueOf(cita.getFecha()),
                java.sql.Date.valueOf(cita.getFecha()),
                java.sql.Time.valueOf(cita.getHora()),
                cita.getObservaciones() != null ? cita.getObservaciones() : "",
                cita.getEstado(),
                uuidCita
            );

            // Si al actualizar se coloca como ATENDIDA, la enviamos al control diario
            if (filas > 0 && "ATENDIDA".equalsIgnoreCase(cita.getEstado())) {
                pasarAControlDiario(cita);
            }

            return filas > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar cita: " + e.getMessage());
            return false;
        }
    }

    private void pasarAControlDiario(Cita cita) {
        try {
            String sqlControl = "INSERT INTO control_diario (uuid_titular, uuid_titular_fam, fecha_control, hora_control, uuid_medico, consultorio, motivo_consulta, status) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, false)";
            
            jdbcTemplate.update(sqlControl,
                cita.getUuidTitular(),
                cita.getUuidTitularFamiliar(),
                java.sql.Date.valueOf(LocalDate.now()),
                java.sql.Time.valueOf(LocalTime.now()),
                cita.getUuidMedico(),
                cita.getUuidConsultorio(),
                !cita.getObservaciones().isEmpty() ? cita.getObservaciones() : "Atención por Cita Médica"
            );
        } catch (Exception e) {
            System.err.println("Aviso: No se pudo replicar la cita al control diario (puede que ya exista o falte un campo): " + e.getMessage());
        }
    }

    @Override
    public boolean eliminarCita(UUID id) {
        String sql = "DELETE FROM citas WHERE uuid_cita = ?::uuid";
        int filas = jdbcTemplate.update(sql, id);
        return filas > 0;
    }
}