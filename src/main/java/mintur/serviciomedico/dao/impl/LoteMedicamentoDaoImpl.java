package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.LoteMedicamentoDao;
import mintur.serviciomedico.model.LoteMedicamento;
import mintur.serviciomedico.model.LoteMedicamentoFull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Repository
public class LoteMedicamentoDaoImpl implements LoteMedicamentoDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LoteMedicamentoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // MÉTODOS DE BÚSQUEDA (SELECTS)
    // ============================================================

    @Override
    public LoteMedicamento buscarPorUUID(String uuid) throws SQLException {
        String sql = "SELECT * FROM medicamento_lotes WHERE uuid_lote = ?::uuid";
        List<LoteMedicamento> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapToSimple(rs), UUID.fromString(uuid));
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<LoteMedicamento> listarLotesDisponibles(String uuidMedicamento) throws SQLException {
        String sql = """
            SELECT * FROM medicamento_lotes 
            WHERE uuid_medicamento = ?::uuid 
              AND stock_disponible > 0 
              AND activo = true 
            ORDER BY fecha_vencimiento ASC, numero_lote ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToSimple(rs), UUID.fromString(uuidMedicamento));
    }

    @Override
    public List<LoteMedicamentoFull> listarTodoDetallado() throws SQLException {
        String sql = "SELECT * FROM medicamento_lotes ORDER BY fecha_ingreso DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToFull(rs));
    }

    @Override
    public List<LoteMedicamentoFull> listarPorFactura(String uuidFactura) throws SQLException {
        String sql = "SELECT * FROM medicamento_lotes WHERE uuid_factura = ? ORDER BY fecha_ingreso ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToFull(rs), uuidFactura);
    }

    @Override
    public List<LoteMedicamentoFull> listarPorProveedor(String proveedor) throws SQLException {
        String sql = "SELECT * FROM medicamento_lotes WHERE proveedor = ? ORDER BY fecha_vencimiento ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapToFull(rs), proveedor);
    }

    @Override
    public boolean existeEntrega(String uuidControl, String uuidMedicamento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM control_diario_medicamento WHERE uuid_control = ?::uuid AND uuid_medicamento = ?::uuid";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, 
                        UUID.fromString(uuidControl), UUID.fromString(uuidMedicamento));
        return count != null && count > 0;
    }
    
    @Override
    public List<LoteMedicamentoFull> listarPorMedicamento(String uuidMedicamento) {
    String sql = "SELECT * FROM medicamento_lotes WHERE uuid_medicamento = ?::uuid";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(LoteMedicamentoFull.class), uuidMedicamento);
}

    // ============================================================
    // MÉTODOS DE ESCRITURA (INSERT / UPDATE)
    // ============================================================

    @Override
    public void insertar(LoteMedicamentoFull lote) throws SQLException {
        String sql = """
            INSERT INTO medicamento_lotes 
            (uuid_lote, uuid_medicamento, uuid_factura, numero_lote, fecha_vencimiento, 
             stock_inicial, stock_disponible, proveedor, observaciones, activo) 
            VALUES (?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        jdbcTemplate.update(sql, 
            UUID.fromString(lote.getUuidLote()),
            UUID.fromString(lote.getUuidMedicamento()),
            (lote.getUuidFactura() != null && !lote.getUuidFactura().isBlank()) ? UUID.fromString(lote.getUuidFactura()) : null,
            lote.getNumeroLote(),
            lote.getFechaVencimiento(),
            lote.getStockInicial(),
            lote.getStockDisponible(),
            lote.getProveedor(),
            lote.getObservaciones(),
            lote.isActivo()
        );
    }

    @Override
    public void actualizar(LoteMedicamentoFull lote) throws SQLException {
        String sql = """
            UPDATE medicamento_lotes 
            SET numero_lote = ?, fecha_vencimiento = ?, proveedor = ?, observaciones = ?, activo = ? 
            WHERE uuid_lote = ?::uuid
        """;
        
        jdbcTemplate.update(sql, 
            lote.getNumeroLote(),
            lote.getFechaVencimiento(),
            lote.getProveedor(),
            lote.getObservaciones(),
            lote.isActivo(),
            UUID.fromString(lote.getUuidLote())
        );
    }

    @Override
    public void actualizarStock(String uuidLote, int cantidad) throws SQLException {
        String sql = """
            UPDATE medicamento_lotes 
            SET stock_disponible = stock_disponible + ?,
                activo = CASE WHEN (stock_disponible + ?) = 0 THEN false ELSE activo END
            WHERE uuid_lote = ?::uuid 
              AND (stock_disponible + ?) >= 0
        """;
        
        int filas = jdbcTemplate.update(sql, cantidad, cantidad, UUID.fromString(uuidLote), cantidad);
        
        if (filas == 0) {
            throw new SQLException("Operación inválida: Stock insuficiente en el lote o ID no encontrado.");
        }
    }

    @Override
    public void inactivar(String uuidLote) throws SQLException {
        String sql = "UPDATE medicamento_lotes SET activo = false WHERE uuid_lote = ?::uuid";
        jdbcTemplate.update(sql, UUID.fromString(uuidLote));
    }
    
    
    @Override
    public void registrarBitacoraGeneral(String usuarioId, String modulo, String accion, 
                                           String anterior, String nuevo, String ip, 
                                           String descripcion, String uuidEntidad, String tabla) throws SQLException {
        String sql = """
            INSERT INTO bitacora_servicom 
            (usuario_id, modulo, accion, valor_anterior, valor_nuevo, ip_terminal, descripcion, entidad_uuid, tabla_afectada) 
            VALUES (?::uuid, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        jdbcTemplate.update(sql, 
            UUID.fromString(usuarioId), 
            modulo, 
            accion, 
            anterior, 
            nuevo, 
            ip, 
            descripcion, 
            uuidEntidad, 
            tabla
        );
    }
    
    // ============================================================
    // MAPEADORES PRIVADOS
    // ============================================================

    private LoteMedicamento mapToSimple(ResultSet rs) throws SQLException {
        LoteMedicamento l = new LoteMedicamento();
        l.setUuidLote(rs.getString("uuid_lote"));
        l.setUuidMedicamento(rs.getString("uuid_medicamento"));
        l.setNumeroLote(rs.getString("numero_lote"));
        l.setStockDisponible(rs.getInt("stock_disponible"));
        
        if (rs.getDate("fecha_vencimiento") != null) {
            l.setFechaVencimiento(rs.getDate("fecha_vencimiento").toLocalDate());
        }
        
        l.setActivo(rs.getBoolean("activo"));
        l.setDescripcionLote("Lote: " + l.getNumeroLote() + " (Disp: " + l.getStockDisponible() + ")");
        return l;
    }

    private LoteMedicamentoFull mapToFull(ResultSet rs) throws SQLException {
        LoteMedicamentoFull l = new LoteMedicamentoFull();
        l.setUuidLote(rs.getString("uuid_lote"));
        l.setUuidMedicamento(rs.getString("uuid_medicamento"));
        l.setUuidFactura(rs.getString("uuid_factura"));
        l.setNumeroLote(rs.getString("numero_lote"));
        l.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        l.setStockInicial(rs.getInt("stock_inicial"));
        l.setStockDisponible(rs.getInt("stock_disponible"));
        l.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        l.setProveedor(rs.getString("proveedor"));
        l.setObservaciones(rs.getString("observaciones"));
        l.setActivo(rs.getBoolean("activo"));
        return l;
    }
}