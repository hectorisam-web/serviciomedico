package mintur.serviciomedico.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ControlDiarioMedicamento {

    // ============================================================
    // CAMPOS DE TABLA (persistentes)
    // ============================================================
    private String uuidControlMed;     // PK
    private String uuidControl;        // FK → control_diario
    private String uuidMedicamento;    // FK → medicamentos
    private String uuidLote;           // FK → medicamento_lotes (nullable)
    private int cantidad;
    private String tratamiento;
    private String observaciones;

    // ============================================================
    // CAMPOS DERIVADOS (solo para UI, no están en la tabla)
    // ============================================================
    private String descripcionMedicamento;
    private String presentacion;
    private String descripcionPresentacion;
    private String numeroLote;
    private LocalDate fechaControl;
    private LocalTime horaControl;
    
    // CAMPOS PARA LA GESTIÓN DE ENTREGAS EN LA TABLA
    private Integer nuevaEntrega = 0; 
    private Integer stockDisponibleActual = 0; // <--- ESTE ES EL QUE FALTABA

    // ============================================================
    // GETTERS / SETTERS
    // ============================================================

    public String getUuidControlMed() { return uuidControlMed; }
    public void setUuidControlMed(String uuidControlMed) { this.uuidControlMed = uuidControlMed; }

    public String getUuidControl() { return uuidControl; }
    public void setUuidControl(String uuidControl) { this.uuidControl = uuidControl; }

    public String getUuidMedicamento() { return uuidMedicamento; }
    public void setUuidMedicamento(String uuidMedicamento) { this.uuidMedicamento = uuidMedicamento; }

    public String getUuidLote() { return uuidLote; }
    public void setUuidLote(String uuidLote) { this.uuidLote = uuidLote; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getDescripcionMedicamento() { return descripcionMedicamento; }
    public void setDescripcionMedicamento(String descripcionMedicamento) { this.descripcionMedicamento = descripcionMedicamento; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public String getDescripcionPresentacion() { return descripcionPresentacion; }
    public void setDescripcionPresentacion(String descripcionPresentacion) { this.descripcionPresentacion = descripcionPresentacion; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public LocalDate getFechaControl() { return fechaControl; }
    public void setFechaControl(LocalDate fechaControl) { this.fechaControl = fechaControl; }

    public LocalTime getHoraControl() { return horaControl; }
    public void setHoraControl(LocalTime horaControl) { this.horaControl = horaControl; }

    // GETTERS Y SETTERS PARA LOS CAMPOS DE LA INTERFAZ
    public Integer getNuevaEntrega() { return nuevaEntrega; }
    public void setNuevaEntrega(Integer nuevaEntrega) { this.nuevaEntrega = nuevaEntrega; }

    public Integer getStockDisponibleActual() { return stockDisponibleActual; }
    public void setStockDisponibleActual(Integer stockDisponibleActual) { this.stockDisponibleActual = stockDisponibleActual; }
}