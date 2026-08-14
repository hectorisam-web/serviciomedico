package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.MaestroDao;
import mintur.serviciomedico.model.ItemMaestro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MaestroDaoImpl implements MaestroDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MaestroDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // MÉTODOS DE CONSULTA Y FILTRADO
    // ============================================================

    @Override
    public ItemMaestro buscarCategoriaRaiz(String descripcionRaiz) throws SQLException {
        String sql = "SELECT * FROM maestro WHERE maestro = 0 AND descripcion = ? ORDER BY id_codigo LIMIT 1";
        List<ItemMaestro> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapear(rs), descripcionRaiz);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<ItemMaestro> listarHijos(String uuidMaestro) throws SQLException {
        String sql = "SELECT * FROM maestro WHERE uuid_maestro = ? ORDER BY id_codigo";
        UUID parentUuid = (uuidMaestro == null || uuidMaestro.trim().isEmpty()) ? null : UUID.fromString(uuidMaestro);
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapear(rs), parentUuid);
    }

    @Override
    public List<ItemMaestro> listarHijosPorDescripcionRaiz(String descripcionRaiz) throws SQLException {
        ItemMaestro raiz = buscarCategoriaRaiz(descripcionRaiz);
        if (raiz == null) return List.of();
        return listarHijos(raiz.getUuid());
    }

    @Override
    public List<ItemMaestro> listarPresentaciones() throws Exception {
        // Mantiene la regla de negocio original (Código maestro 400)
        String sql = "SELECT * FROM maestro WHERE maestro = 400 ORDER BY id_codigo";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapear(rs));
    }

    @Override
    public ItemMaestro buscarPorUUID(String uuid) throws SQLException {
        String sql = "SELECT * FROM maestro WHERE uuid = ?";
        List<ItemMaestro> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> mapear(rs), UUID.fromString(uuid));
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<ItemMaestro> listarPadres() throws SQLException {
        String sql = "SELECT * FROM maestro WHERE maestro = 0 ORDER BY id_codigo";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapear(rs));
    }

    // ============================================================
    // MÉTODOS DE ESCRITURA Y ESPECÍFICOS DE ADMINISTRACIÓN
    // ============================================================

    @Override
    public void insertar(ItemMaestro m) throws SQLException {
        String sql = """
            INSERT INTO maestro (uuid, uuid_maestro, id_codigo, descripcion, maestro, ordinal, estatus, traduccion) 
            VALUES (gen_random_uuid(), ?, ?, ?, ?, ?, ?, ?)
        """;
        
        UUID parentUuid = (m.getUuidMaestro() == null || m.getUuidMaestro().trim().isEmpty()) 
                          ? null : UUID.fromString(m.getUuidMaestro());

        jdbcTemplate.update(sql, 
            parentUuid,
            m.getIdCodigo(),
            m.getDescripcion(),
            m.getMaestro(),
            m.getOrdinal(),
            m.getEstatus(),
            m.getTraduccion()
        );
    }
    
    @Override
    public void actualizar(ItemMaestro m) throws SQLException {
        // Incluye resguardo integral de la jerarquía ante eventuales traslados de rama
        String sql = """
            UPDATE maestro SET uuid_maestro = ?, id_codigo = ?, descripcion = ?, 
                               maestro = ?, ordinal = ?, estatus = ?, traduccion = ? 
            WHERE uuid = ?
        """;

        UUID parentUuid = (m.getUuidMaestro() == null || m.getUuidMaestro().trim().isEmpty()) 
                          ? null : UUID.fromString(m.getUuidMaestro());

        jdbcTemplate.update(sql,
            parentUuid,
            m.getIdCodigo(),
            m.getDescripcion(),
            m.getMaestro(),
            m.getOrdinal(),
            m.getEstatus(),
            m.getTraduccion(),
            UUID.fromString(m.getUuid())
        );
    }

    @Override
    public void eliminar(String uuid) throws SQLException {
        String sql = "DELETE FROM maestro WHERE uuid = ?";
        jdbcTemplate.update(sql, UUID.fromString(uuid));
    }

    @Override
    public int obtenerSiguienteOrdinal(String uuidPadre) throws SQLException {
        String sql;
        UUID parentUuid = (uuidPadre == null || uuidPadre.trim().isEmpty()) ? null : UUID.fromString(uuidPadre);
        
        // Blindaje dinámico: Postgres requiere la sintaxis 'IS NULL' para raíces
        if (parentUuid == null) {
            sql = "SELECT COALESCE(MAX(ordinal), 0) + 1 FROM maestro WHERE uuid_maestro IS NULL";
            Integer nextOrd = jdbcTemplate.queryForObject(sql, Integer.class);
            return nextOrd != null ? nextOrd : 1;
        } else {
            sql = "SELECT COALESCE(MAX(ordinal), 0) + 1 FROM maestro WHERE uuid_maestro = ?";
            Integer nextOrd = jdbcTemplate.queryForObject(sql, Integer.class, parentUuid);
            return nextOrd != null ? nextOrd : 1;
        }
    }

    // ============================================================
    // MAPEO INTERNO DE ENTRADAS (RESULTSET -> OBJETO)
    // ============================================================

    private ItemMaestro mapear(ResultSet rs) throws SQLException {
        ItemMaestro m = new ItemMaestro();
        m.setUuid(rs.getString("uuid"));
        m.setUuidMaestro(rs.getString("uuid_maestro"));
        m.setIdCodigo(rs.getInt("id_codigo"));
        m.setDescripcion(rs.getString("descripcion"));
        
        // Control riguroso de tipos primitivos numéricos y booleanos que admiten nulos en BD
        int maestroVal = rs.getInt("maestro");
        m.setMaestro(rs.wasNull() ? null : maestroVal);
        
        int ordinalVal = rs.getInt("ordinal");
        m.setOrdinal(rs.wasNull() ? null : ordinalVal);
        
        boolean estatusVal = rs.getBoolean("estatus");
        m.setEstatus(rs.wasNull() ? null : estatusVal);
        
        m.setTraduccion(rs.getString("traduccion"));
        return m;
    }
}