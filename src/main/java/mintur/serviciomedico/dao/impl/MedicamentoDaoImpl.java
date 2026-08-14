package mintur.serviciomedico.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import mintur.serviciomedico.dao.MedicamentoDao;
import mintur.serviciomedico.model.ItemMaestro;
import mintur.serviciomedico.model.Medicamento;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MedicamentoDaoImpl implements MedicamentoDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MedicamentoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Mapper reutilizable para convertir filas de la base de datos en objetos Medicamento.
     * Corregido para incluir la gestión de stocks, topes y la flag de tratamiento continuo.
     */
    private final RowMapper<Medicamento> medicamentoMapper = (rs, rowNum) -> {
        Medicamento m = new Medicamento();
        m.setUuidMedicamento(rs.getString("uuid_medicamento"));
        m.setDescripcionMedicamento(rs.getString("descripcionmedicamento"));
        m.setUuidPresentacion(rs.getString("uuid_presentacion"));
        String descPresentacion = rs.getString("descripcion_presentacion");
        m.setDescripcionPresentacion(descPresentacion != null ? descPresentacion : "Sin presentación");
                
        // Mapeo seguro de enteros (Wrappers) para evitar ceros falsos en valores nulos
        m.setStockMaximo(rs.getObject("stock_maximo") != null ? rs.getInt("stock_maximo") : null);
        m.setStockMinimo(rs.getObject("stock_minimo") != null ? rs.getInt("stock_minimo") : null);
        m.setStockActual(rs.getObject("stock_actual") != null ? rs.getInt("stock_actual") : null);
        m.setMaximoPorEntrega(rs.getObject("maximo_por_entrega") != null ? rs.getInt("maximo_por_entrega") : 30);
        
        m.setRequiereTratamiento(rs.getBoolean("requiere_tratamiento"));
        m.setActivo(rs.getBoolean("activo"));
        return m;
    };

    @Override
    public List<Medicamento> listar() throws SQLException {
        String sql = """
            SELECT m.uuid_medicamento, m.descripcionmedicamento, m.uuid_presentacion,
                   m.stock_maximo, m.stock_minimo, m.stock_actual, m.maximo_por_entrega,
                   m.requiere_tratamiento, m.activo, p.descripcion AS descripcion_presentacion
            FROM medicamentos m
            LEFT JOIN maestro p ON p.uuid = m.uuid_presentacion
            ORDER BY m.descripcionmedicamento
        """;
        return jdbcTemplate.query(sql, medicamentoMapper);
    }

    @Override
    public void insertar(Medicamento m) throws SQLException {
        String sql = """
            INSERT INTO medicamentos (
                uuid_medicamento, descripcionmedicamento, uuid_presentacion, 
                stock_maximo, stock_minimo, stock_actual, maximo_por_entrega, 
                requiere_tratamiento, activo
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        UUID nuevoUuid = (m.getUuidMedicamento() != null && !m.getUuidMedicamento().isBlank()) 
                         ? UUID.fromString(m.getUuidMedicamento()) 
                         : UUID.randomUUID();

        jdbcTemplate.update(sql, 
            nuevoUuid, 
            m.getDescripcionMedicamento() != null ? m.getDescripcionMedicamento().toUpperCase().trim() : null, 
            m.getUuidPresentacion() != null ? UUID.fromString(m.getUuidPresentacion()) : null, 
            m.getStockMaximo(),
            m.getStockMinimo(),
            m.getStockActual(),
            m.getMaximoPorEntrega(),
            m.isRequiereTratamiento(),
            m.isActivo()
        );
    }

    @Override
    public void actualizar(Medicamento m) throws SQLException {
        String sql = """
            UPDATE medicamentos
            SET descripcionmedicamento = ?, uuid_presentacion = ?, 
                stock_maximo = ?, stock_minimo = ?, stock_actual = ?, 
                maximo_por_entrega = ?, requiere_tratamiento = ?, activo = ?
            WHERE uuid_medicamento = ?
        """;
        
        jdbcTemplate.update(sql, 
            m.getDescripcionMedicamento() != null ? m.getDescripcionMedicamento().toUpperCase().trim() : null, 
            m.getUuidPresentacion() != null ? UUID.fromString(m.getUuidPresentacion()) : null, 
            m.getStockMaximo(),
            m.getStockMinimo(),
            m.getStockActual(),
            m.getMaximoPorEntrega(),
            m.isRequiereTratamiento(),
            m.isActivo(), 
            UUID.fromString(m.getUuidMedicamento())
        );
    }

    @Override
    public boolean existeDescripcion(String descripcion) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medicamentos WHERE LOWER(descripcionmedicamento) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, descripcion);
        return count != null && count > 0;
    }

    @Override
    public List<ItemMaestro> listarPresentaciones() throws SQLException {
        String sql = "SELECT uuid, descripcion FROM maestro WHERE estatus = true AND uuid_maestro = ? ORDER BY descripcion";
        UUID catPresentaciones = UUID.fromString("38828c89-1afb-499f-8c7c-5cc933ff9ff2");

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ItemMaestro item = new ItemMaestro();
            item.setUuid(rs.getString("uuid"));
            item.setDescripcion(rs.getString("descripcion"));
            return item;
        }, catPresentaciones);
    }

    @Override
    public List<String> buscarDescripcionLike(String filtro) throws SQLException {
        String sql = "SELECT descripcionmedicamento FROM medicamentos WHERE LOWER(descripcionmedicamento) LIKE LOWER(?)";
        return jdbcTemplate.queryForList(sql, String.class, "%" + filtro + "%");
    }

    @Override
    public Medicamento buscarPorDescripcionExacta(String descripcion) throws SQLException {
        String sql = """
            SELECT m.uuid_medicamento, m.descripcionmedicamento, m.uuid_presentacion,
                   m.stock_maximo, m.stock_minimo, m.stock_actual, m.maximo_por_entrega,
                   m.requiere_tratamiento, m.activo, p.descripcion AS descripcion_presentacion
            FROM medicamentos m
            LEFT JOIN maestro p ON p.uuid = m.uuid_presentacion
            WHERE LOWER(m.descripcionmedicamento) = LOWER(?)
        """;
        
        List<Medicamento> resultados = jdbcTemplate.query(sql, medicamentoMapper, descripcion);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public Medicamento buscarPorUUID(String uuid) throws SQLException {
        String sql = """
            SELECT m.uuid_medicamento, m.descripcionmedicamento, m.uuid_presentacion,
                   m.stock_maximo, m.stock_minimo, m.stock_actual, m.maximo_por_entrega,
                   m.requiere_tratamiento, m.activo, p.descripcion AS descripcion_presentacion
            FROM medicamentos m
            LEFT JOIN maestro p ON p.uuid = m.uuid_presentacion
            WHERE m.uuid_medicamento = ?
        """;

        List<Medicamento> resultados = jdbcTemplate.query(sql, medicamentoMapper, UUID.fromString(uuid));
        return resultados.isEmpty() ? null : resultados.get(0);
    }
}