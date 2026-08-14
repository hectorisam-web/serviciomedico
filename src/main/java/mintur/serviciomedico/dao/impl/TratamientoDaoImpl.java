package mintur.serviciomedico.dao.impl;

import mintur.serviciomedico.dao.TratamientoDao;
import mintur.serviciomedico.model.Tratamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TratamientoDaoImpl implements TratamientoDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TratamientoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper para convertir las filas de la tabla tratamientos a objetos del modelo.
     */
    private final RowMapper<Tratamiento> tratamientoMapper = (rs, rowNum) -> {
        Tratamiento t = new Tratamiento();
        t.setUuidTratamiento((UUID) rs.getObject("uuid_tratamiento"));
        t.setUuidControl((UUID) rs.getObject("uuid_control"));
        t.setUuidMedicamento((UUID) rs.getObject("uuid_medicamento"));
        t.setTipoTratamiento(rs.getString("tipo_tratamiento"));
        t.setCantidadPorToma((Integer) rs.getObject("cantidad_por_toma"));
        t.setIntervaloHoras((Integer) rs.getObject("intervalo_horas"));
        t.setDuracionDias((Integer) rs.getObject("duracion_dias"));
        t.setHorarios(rs.getString("horarios"));
        t.setCantidadTotal(rs.getInt("cantidad_total"));
        t.setObservaciones(rs.getString("observaciones"));
        t.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        return t;
    };

    @Override
    public boolean insertar(Tratamiento t) {
        String sql = """
            INSERT INTO tratamientos (
                uuid_tratamiento, uuid_control, uuid_medicamento, tipo_tratamiento,
                cantidad_por_toma, intervalo_horas, duracion_dias, horarios,
                cantidad_total, observaciones
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        int rows = jdbcTemplate.update(sql,
            t.getUuidTratamiento(),
            t.getUuidControl(),
            t.getUuidMedicamento(),
            t.getTipoTratamiento(),
            t.getCantidadPorToma(),
            t.getIntervaloHoras(),
            t.getDuracionDias(),
            t.getHorarios(),
            t.getCantidadTotal(),
            t.getObservaciones()
        );

        return rows > 0;
    }

    @Override
    public List<Tratamiento> listarPorControl(UUID uuidControl) {
        String sql = "SELECT * FROM tratamientos WHERE uuid_control = ? ORDER BY fecha_registro ASC";
        return jdbcTemplate.query(sql, tratamientoMapper, uuidControl);
    }

    @Override
    public Tratamiento buscarPorControlYMedicamento(UUID uuidControl, UUID uuidMedicamento) {
        String sql = """
            SELECT * FROM tratamientos
            WHERE uuid_control = ? AND uuid_medicamento = ?
            ORDER BY fecha_registro DESC
            LIMIT 1
        """;
        try {
            return jdbcTemplate.queryForObject(sql, tratamientoMapper, uuidControl, uuidMedicamento);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}