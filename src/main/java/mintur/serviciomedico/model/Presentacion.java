package mintur.serviciomedico.model;

import java.util.UUID;

public class Presentacion {
    private UUID uuidPresentacion;
    private String descripcionPresentacion;
    private boolean activo;

    public Presentacion() {}

    public Presentacion(UUID uuidPresentacion, String descripcionPresentacion, boolean activo) {
        this.uuidPresentacion = uuidPresentacion;
        this.descripcionPresentacion = descripcionPresentacion;
        this.activo = activo;
    }

    public UUID getUuidPresentacion() {
        return uuidPresentacion;
    }

    public void setUuidPresentacion(UUID uuidPresentacion) {
        this.uuidPresentacion = uuidPresentacion;
    }

    public String getDescripcionPresentacion() {
        return descripcionPresentacion;
    }

    public void setDescripcionPresentacion(String descripcionPresentacion) {
        this.descripcionPresentacion = descripcionPresentacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
