package mintur.serviciomedico.model;

import java.sql.Date;
import java.util.UUID;

public class Factura {

    private UUID uuidFactura;
    private String numeroFactura;
    private Date fechaFactura;     // ← YA NO ES LocalDate
    private String proveedor;
    private String observaciones;
    private Boolean activo;

    public Factura() {}

    public UUID getUuidFactura() {
        return uuidFactura;
    }

    public void setUuidFactura(UUID uuidFactura) {
        this.uuidFactura = uuidFactura;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Date getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(Date fechaFactura) {
        this.fechaFactura = fechaFactura;
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

    public Boolean getActivo() {
        return activo;
    }

    public boolean isActivo() {
        return activo != null && activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "N° " + numeroFactura + " – " + fechaFactura;
    }
}
