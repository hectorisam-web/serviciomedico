package mintur.serviciomedico.model;

public class ItemMaestro {

    private Integer idCodigo;     // Código interno del maestro
    private String uuid;          // PK real
    private String uuidMaestro;   // UUID del maestro padre
    private String descripcion;   // Texto visible
    private Integer maestro;      // Tipo de maestro (opcional)
    private Integer ordinal;      // Orden de presentación
    private Boolean estatus;      // Activo/inactivo
    private String traduccion;    // Traducción opcional

    public ItemMaestro() {
    }

    public ItemMaestro(String uuid, String descripcion) {
        this.uuid = uuid;
        this.descripcion = descripcion;
    }

    public ItemMaestro(Integer idCodigo, String uuid, String descripcion) {
        this.idCodigo = idCodigo;
        this.uuid = uuid;
        this.descripcion = descripcion;
    }

    // ---------------------------------------------------------
    // Getters y Setters
    // ---------------------------------------------------------

    public Integer getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(Integer idCodigo) {
        this.idCodigo = idCodigo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuidMaestro() {
        return uuidMaestro;
    }

    public void setUuidMaestro(String uuidMaestro) {
        this.uuidMaestro = uuidMaestro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getMaestro() {
        return maestro;
    }

    public void setMaestro(Integer maestro) {
        this.maestro = maestro;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }

    public String getTraduccion() {
        return traduccion;
    }

    public void setTraduccion(String traduccion) {
        this.traduccion = traduccion;
    }

    // ---------------------------------------------------------
    // Alias requerido por MedicamentoDaoImpl
    // ---------------------------------------------------------

    public void setCodigo(String uuid) {
        this.uuid = uuid;
    }

    public String getCodigo() {
        return uuid;
    }

    // ---------------------------------------------------------

    @Override
    public String toString() {
        return descripcion;
    }
}
