package mintur.serviciomedico.model;

import java.util.UUID;

public class Maestro {
    private UUID uuid;
    private UUID uuidMaestro; // Relación jerárquica (ID del padre)
    private Integer idCodigo;
    private String descripcion;
    private Integer maestro;  // Código numérico si manejan lógica heredada
    private Integer ordinal;  // Para ordenar las listas en los combos
    private Boolean estatus;
    private String traduccion;

    // Constructores
    public Maestro() {}

    // Getters y Setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public UUID getUuidMaestro() { return uuidMaestro; }
    public void setUuidMaestro(UUID uuidMaestro) { this.uuidMaestro = uuidMaestro; }

    public Integer getIdCodigo() { return idCodigo; }
    public void setIdCodigo(Integer idCodigo) { this.idCodigo = idCodigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getMaestro() { return maestro; }
    public void setMaestro(Integer maestro) { this.maestro = maestro; }

    public Integer getOrdinal() { return ordinal; }
    public void setOrdinal(Integer ordinal) { this.ordinal = ordinal; }

    public Boolean getEstatus() { return estatus; }
    public void setEstatus(Boolean estatus) { this.estatus = estatus; }

    public String getTraduccion() { return traduccion; }
    public void setTraduccion(String traduccion) { this.traduccion = traduccion; }

    @Override
    public String toString() {
        return "Maestro{" +
                "uuid=" + uuid +
                ", descripcion='" + descripcion + '\'' +
                ", idCodigo=" + idCodigo +
                ", estatus=" + estatus +
                '}';
    }
}