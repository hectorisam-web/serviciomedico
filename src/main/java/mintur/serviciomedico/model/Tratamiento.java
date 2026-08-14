package mintur.serviciomedico.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Tratamiento {

    private UUID uuidTratamiento;
    private UUID uuidControl;
    private UUID uuidMedicamento;

    private String tipoTratamiento;      // "posologia" o "horarios"

    // POSOLOGÍA CLÁSICA
    private Integer cantidadPorToma;     // Ej: 1, 2
    private Integer intervaloHoras;      // Ej: 8, 12, 24
    private Integer duracionDias;        // Ej: 3, 5, 7

    // HORARIOS FIJOS
    private String horarios;             // Ej: "08:00,20:00"

    // Cálculo final
    private Integer cantidadTotal;       // Unidades reales a entregar

    private String observaciones;
    private Timestamp fechaRegistro;

    // Constructor vacío
    public Tratamiento() {}

    // Getters y Setters
    public UUID getUuidTratamiento() {
        return uuidTratamiento;
    }

    public void setUuidTratamiento(UUID uuidTratamiento) {
        this.uuidTratamiento = uuidTratamiento;
    }

    public UUID getUuidControl() {
        return uuidControl;
    }

    public void setUuidControl(UUID uuidControl) {
        this.uuidControl = uuidControl;
    }

    public UUID getUuidMedicamento() {
        return uuidMedicamento;
    }

    public void setUuidMedicamento(UUID uuidMedicamento) {
        this.uuidMedicamento = uuidMedicamento;
    }

    public String getTipoTratamiento() {
        return tipoTratamiento;
    }

    public void setTipoTratamiento(String tipoTratamiento) {
        this.tipoTratamiento = tipoTratamiento;
    }

    public Integer getCantidadPorToma() {
        return cantidadPorToma;
    }

    public void setCantidadPorToma(Integer cantidadPorToma) {
        this.cantidadPorToma = cantidadPorToma;
    }

    public Integer getIntervaloHoras() {
        return intervaloHoras;
    }

    public void setIntervaloHoras(Integer intervaloHoras) {
        this.intervaloHoras = intervaloHoras;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
