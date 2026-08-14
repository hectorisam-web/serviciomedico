package mintur.serviciomedico.dao.impl;

import java.sql.Date;
import mintur.serviciomedico.dao.ControlDiarioMedicamentoDao;
import mintur.serviciomedico.model.ControlDiarioMedicamento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class ControlDiarioMedicamentoDaoImpl implements ControlDiarioMedicamentoDao {

    private final JdbcTemplate jdbcTemplate;

    public ControlDiarioMedicamentoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // INSERTAR ENTREGA
    // ============================================================
    @Override
    public void insertar(ControlDiarioMedicamento m) throws SQLException {
        String sql = """
            INSERT INTO control_diario_medicamento
            (uuid_control, uuid_medicamento, uuid_lote, cantidad, tratamiento, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        Object uuidLote = (m.getUuidLote() == null || m.getUuidLote().isBlank()) 
                        ? null : UUID.fromString(m.getUuidLote());

        jdbcTemplate.update(sql, 
            UUID.fromString(m.getUuidControl()),
            UUID.fromString(m.getUuidMedicamento()),
            uuidLote,
            m.getCantidad(),
            m.getTratamiento(),
            m.getObservaciones()
        );
    }

    // ============================================================
    // LISTAR POR CONTROL
    // ============================================================
    @Override
public List<ControlDiarioMedicamento> listarPorControl(String uuidControl) throws SQLException {
    String sql = """
        SELECT 
            cdm.uuid_control_med, cdm.uuid_control, cdm.uuid_medicamento, cdm.uuid_lote,
            ml.numero_lote, cdm.cantidad, cdm.tratamiento, cdm.observaciones,
            m.descripcionmedicamento, p.descripcion AS presentacion,cd.fecha_control
        FROM control_diario_medicamento cdm
        LEFT JOIN control_diario cd ON cd.uuid_control = cdm.uuid_control
        LEFT JOIN medicamento_lotes ml ON ml.uuid_lote = cdm.uuid_lote
        LEFT JOIN medicamentos m ON m.uuid_medicamento = cdm.uuid_medicamento
        LEFT JOIN maestro p ON m.uuid_presentacion = p.uuid
        WHERE cdm.uuid_control = ?::uuid
        ORDER BY cdm.uuid_control_med
    """;

    System.out.println("=== [DEBUG] Listar por Control ID: " + uuidControl);
    
    List<ControlDiarioMedicamento> lista = jdbcTemplate.query(sql, (rs, rowNum) -> mapearFull(rs), uuidControl);
    
    System.out.println("=== [DEBUG] Filas encontradas por JDBC: " + (lista != null ? lista.size() : "null"));
    return lista;
}
    
   
    // ============================================================
    // LISTAR POR PACIENTE (Historial)
    // ============================================================
    @Override
    public List<ControlDiarioMedicamento> listarPorPaciente(String uuidTitular, String uuidFamiliar) throws SQLException {
        boolean tieneFamiliar = uuidFamiliar != null && !uuidFamiliar.isBlank();
        
        String sql = """
            SELECT 
                cdm.*, 
                cd.uuid_titular, 
                cd.uuid_titular_fam, 
                ml.numero_lote, 
                m.descripcionmedicamento, 
                p.descripcion AS presentacion,
                cd.fecha_control, 
                cd.hora_control
            FROM control_diario_medicamento cdm
            LEFT JOIN control_diario cd ON cd.uuid_control = cdm.uuid_control
            LEFT JOIN medicamentos m ON m.uuid_medicamento = cdm.uuid_medicamento
            LEFT JOIN maestro p ON p.uuid = m.uuid_presentacion
            LEFT JOIN medicamento_lotes ml ON ml.uuid_lote = cdm.uuid_lote
            WHERE 
            """ + (tieneFamiliar ? "cd.uuid_titular_fam = ?" : "cd.uuid_titular = ? AND cd.uuid_titular_fam IS NULL") + """
            ORDER BY cd.fecha_control DESC, cd.hora_control DESC
        """;

        List<ControlDiarioMedicamento> resultado;

        if (tieneFamiliar) {
            // Si hay familiar, solo pasamos 1 parámetro (el del familiar)
            resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
                ControlDiarioMedicamento m = mapearFull(rs);
                Date fecha = rs.getDate("fecha_control");
                if (m != null) {
                    m.setFechaControl(fecha != null ? fecha.toLocalDate() : null);
                }
                return m;
            }, UUID.fromString(uuidFamiliar));
        } else {
            // Si NO hay familiar, solo pasamos 1 parámetro (el del titular)
            resultado = jdbcTemplate.query(sql, (rs, rowNum) -> {
                ControlDiarioMedicamento m = mapearFull(rs);
                Date fecha = rs.getDate("fecha_control");
                if (m != null) {
                    m.setFechaControl(fecha != null ? fecha.toLocalDate() : null);
                }
                return m;
            }, UUID.fromString(uuidTitular));
        }

        return resultado;
    }
    
    
    
    // ============================================================
    // EXISTE ENTREGA
    // ============================================================
    @Override
    public boolean existeEntrega(String uuidControl, String uuidMedicamento, String uuidLote) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM control_diario_medicamento
            WHERE uuid_control = ? AND uuid_medicamento = ? AND uuid_lote = ? AND cantidad > 0
        """;

        Object loteParam = (uuidLote == null || uuidLote.isBlank()) 
                           ? null : UUID.fromString(uuidLote);

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
            UUID.fromString(uuidControl), 
            UUID.fromString(uuidMedicamento), 
            loteParam
        );

        return count != null && count > 0;
    }

    /**
     * Mapeo privado para evitar duplicidad de código
     */
    private ControlDiarioMedicamento mapearFull(java.sql.ResultSet rs) throws SQLException {
        ControlDiarioMedicamento m = new ControlDiarioMedicamento();
        m.setUuidControlMed(rs.getString("uuid_control_med"));
        m.setUuidControl(rs.getString("uuid_control"));
        m.setUuidMedicamento(rs.getString("uuid_medicamento"));
        m.setUuidLote(rs.getString("uuid_lote"));
        m.setNumeroLote(rs.getString("numero_lote"));
        m.setCantidad(rs.getInt("cantidad"));
        m.setTratamiento(rs.getString("tratamiento") != null ? rs.getString("tratamiento") : "");
        m.setObservaciones(rs.getString("observaciones") != null ? rs.getString("observaciones") : "");
        m.setDescripcionMedicamento(rs.getString("descripcionmedicamento"));
        m.setDescripcionPresentacion(rs.getString("presentacion"));
        // Asignar la fecha de control de forma segura
        java.sql.Date fecha = rs.getDate("fecha_control");
        m.setFechaControl(fecha != null ? fecha.toLocalDate() : null);
        return m;
    }
}