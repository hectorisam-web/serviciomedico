package mintur.serviciomedico.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mintur.serviciomedico.dao.TitularDao;
import mintur.serviciomedico.model.Titular;
import mintur.serviciomedico.service.TitularService;
import mintur.serviciomedico.service.BitacoraService;

@Service
public class TitularServiceImpl implements TitularService {

    private final TitularDao dao;
    private final BitacoraService bitacora;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TitularServiceImpl(TitularDao dao, BitacoraService bitacora, JdbcTemplate jdbcTemplate) {
        this.dao = dao;
        this.bitacora = bitacora;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Titular buscarPorCedula(Integer cedula) throws SQLException {
        return dao.buscarPorCedula(cedula);
    }

    @Override
    public Titular buscarPorUuid(String uuid) throws SQLException {
        return dao.buscarPorUuid(uuid);
    }

    @Override
    public List<Titular> obtenerTodos() throws SQLException {
        return dao.obtenerTodos();
    }

    @Override
    public List<Object> listarGrupoFamiliar(String uuid) throws SQLException {
        return dao.listarGrupoFamiliar(uuid);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void guardar(Titular t, UUID operadorId) throws SQLException {
        String accion;
        String valorAnterior = "";
        String valorNuevo = t.toString(); 

        if (t.getUuidTitular() == null) {
            accion = "INSERT";
            dao.insertar(t);
        } else {
            accion = "UPDATE";
            Titular viejo = dao.buscarPorUuid(t.getUuidTitular());
            valorAnterior = (viejo != null) ? viejo.toString() : "";
            dao.actualizar(t);
        }

        bitacora.registrar(
            operadorId, 
            "MAESTRO_TITULARES", 
            accion, 
            valorAnterior, 
            valorNuevo, 
            "127.0.0.1", 
            "Procesamiento de titular: " + t.getNombreTitular(),
            t.getUuidTitular() != null ? UUID.fromString(t.getUuidTitular()) : null, 
            "TITULAR"
        );
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public void eliminar(String uuid, UUID operadorId) throws SQLException {
        Titular t = dao.buscarPorUuid(uuid);
        if (t != null) {
            dao.eliminar(uuid); 
            
            bitacora.registrar(
                operadorId, 
                "MAESTRO_TITULARES", 
                "DELETE", 
                "Status: Activo", 
                "Status: Inactivo", 
                "127.0.0.1", 
                "Inhabilitación de titular: " + t.getNombreTitular(),
                UUID.fromString(uuid), 
                "TITULAR"
            );
        }
    }

    @Override
    public void insertar(Titular t) throws SQLException {
        dao.insertar(t);
    }

    @Override
    public void actualizar(Titular t) throws SQLException {
        dao.actualizar(t);
    }

    // ==========================================================
    // MÉTODO DE BÚSQUEDA DINÁMICA (ACTUALIZADO Y ORDENADO)
    // ==========================================================
    @Override
    public List<Titular> buscarPorNombre(String filtro) throws SQLException {
        // 1. CAST a TEXT para que la búsqueda por número no rompa el filtro JWT
        // 2. LEFT JOIN a 'maestro' para traer descripciones legibles
        // 3. ORDER BY cedula_titular ASC para cumplir el requerimiento visual
        String sql = "SELECT t.*, o.descripcion AS nombre_organismo, s.descripcion AS nombre_sexo " +
                     "FROM titular t " +
                     "LEFT JOIN maestro o ON t.uuid_organismo = o.uuid " +
                     "LEFT JOIN maestro s ON t.uuid_sexo = s.uuid " +
                     "WHERE CAST(t.cedula_titular AS TEXT) ILIKE ? " + 
                     "OR t.nombre_titular ILIKE ? " + 
                     "OR t.apellidos_titular ILIKE ? " +
                     "ORDER BY t.cedula_titular ASC";
        
        String valorBusqueda = "%" + filtro + "%";
        
        return jdbcTemplate.query(sql, new TitularRowMapper(), valorBusqueda, valorBusqueda, valorBusqueda);
    }

    // ==========================================================
    // ROWMAPPER INTERNO
    // ==========================================================
    private static class TitularRowMapper implements RowMapper<Titular> {
        @Override
        public Titular mapRow(ResultSet rs, int rowNum) throws SQLException {
            Titular t = new Titular();
            t.setUuidTitular(rs.getString("uuid_titular"));
            t.setCedulaTitular(rs.getInt("cedula_titular"));
            t.setNombreTitular(rs.getString("nombre_titular"));
            t.setApellidosTitular(rs.getString("apellidos_titular"));
            
            if (rs.getDate("fecha_nacimiento") != null) {
               t.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
            }
            
            t.setNombreOrganismo(rs.getString("nombre_organismo"));
            t.setNombreSexo(rs.getString("nombre_sexo"));
            t.setStatus(rs.getBoolean("status"));
            return t;
        }
    }
}