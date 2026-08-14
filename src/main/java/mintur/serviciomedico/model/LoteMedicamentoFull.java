package mintur.serviciomedico.model;

import java.util.Date;
import java.sql.Timestamp;

/**
 * Esta clase extiende de LoteMedicamento para agregar los campos
 * administrativos (Facturas, Proveedores, Auditoría).
 */
public class LoteMedicamentoFull extends LoteMedicamento {

    private String uuidFactura;
    private String proveedor;
    private String observaciones;
    private Timestamp fechaIngreso;
    // Usamos Date para compatibilidad con los métodos antiguos de JDBC si es necesario,
    // o puedes manejarlo con LocalDate si prefieres consistencia total.
    private Date fechaVencimientoDate; 

    public LoteMedicamentoFull() {
        super();
    }

    // --- GETTERS Y SETTERS ADICIONALES ---

    public String getUuidFactura() {
        return uuidFactura;
    }

    public void setUuidFactura(String uuidFactura) {
        this.uuidFactura = uuidFactura;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    // Método puente para evitar conflictos entre Date y LocalDate en los mapeos
    public void setFechaVencimiento(Date fecha) {
        this.fechaVencimientoDate = fecha;
        if (fecha != null) {
            // Sincroniza con el LocalDate de la clase padre
            super.setFechaVencimiento(new java.sql.Date(fecha.getTime()).toLocalDate());
        }
    }
}