package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.FacturaDao;
import mintur.serviciomedico.model.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class FacturaDaoImpl implements FacturaDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FacturaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
       // System.out.println(">>> DAO CARGADO: FacturaDaoImpl (Modo Spring Boot con JdbcTemplate)");
    }

    @Override
    public void guardar(Factura factura) {
        String sql = """
            INSERT INTO factura (uuid_factura, numero_factura, fecha_factura, proveedor, observaciones, activo) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        UUID id = factura.getUuidFactura() != null ? factura.getUuidFactura() : UUID.randomUUID();
        factura.setUuidFactura(id);

        jdbcTemplate.update(sql,
            id,
            factura.getNumeroFactura(),
            factura.getFechaFactura(),
            factura.getProveedor(),
            factura.getObservaciones(),
            factura.getActivo() != null ? factura.getActivo() : true
        );
    }

    @Override
    public List<Factura> listarTodas() {
        String sql = "SELECT * FROM factura WHERE activo = true ORDER BY fecha_factura DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapearResultSet(rs));
    }

    @Override
    public Factura buscarPorNumero(String numeroFactura) {
        String sql = "SELECT * FROM factura WHERE numero_factura = ?";
        List<Factura> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapearResultSet(rs), numeroFactura);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public Factura buscarPorId(UUID uuid) {
        String sql = "SELECT * FROM factura WHERE uuid_factura = ?";
        List<Factura> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapearResultSet(rs), uuid);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public void actualizar(Factura factura) {
        String sql = """
            UPDATE factura SET numero_factura=?, fecha_factura=?, proveedor=?, observaciones=?, activo=? 
            WHERE uuid_factura=?
        """;

        jdbcTemplate.update(sql,
            factura.getNumeroFactura(),
            factura.getFechaFactura(),
            factura.getProveedor(),
            factura.getObservaciones(),
            factura.getActivo(),
            factura.getUuidFactura()
        );
    }

    @Override
    public void eliminar(UUID uuid) {
        String sql = "UPDATE factura SET activo=false WHERE uuid_factura=?";
        jdbcTemplate.update(sql, uuid);
    }

    /**
     * Mapeo centralizado. 
     */
    private Factura mapearResultSet(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setUuidFactura(rs.getObject("uuid_factura", UUID.class));
        f.setNumeroFactura(rs.getString("numero_factura"));
        f.setFechaFactura(rs.getDate("fecha_factura"));
        f.setProveedor(rs.getString("proveedor"));
        f.setObservaciones(rs.getString("observaciones"));
        f.setActivo(rs.getBoolean("activo"));
        return f;
    }
}