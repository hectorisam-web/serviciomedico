package mintur.serviciomedico.model;

public class Medicamento {

    private String uuidMedicamento;
    private String descripcionMedicamento;
    private String uuidPresentacion;
    private Integer stockMaximo;
    private Integer stockMinimo;
    private Integer stockActual;
    private String descripcionPresentacion;
    private Integer maximoPorEntrega;
    private boolean requiereTratamiento;
    private boolean activo;   // ← Campo requerido por el DAO

    public Medicamento() {
    }

    public Medicamento(String uuidMedicamento, String descripcionMedicamento, String uuidPresentacion,
                       Integer stockMaximo, Integer stockMinimo, Integer stockActual,
                       String descripcionPresentacion, Integer maximoPorEntrega,  
                       boolean requiereTratamiento, boolean activo) {
        this.uuidMedicamento = uuidMedicamento;
        this.descripcionMedicamento = descripcionMedicamento;
        this.uuidPresentacion = uuidPresentacion;
        this.stockMaximo = stockMaximo;
        this.stockMinimo = stockMinimo;
        this.stockActual = stockActual;
        this.descripcionPresentacion = descripcionPresentacion;
        this.maximoPorEntrega = maximoPorEntrega;
        this.requiereTratamiento = requiereTratamiento;
        this.activo = activo;
    }

    // ---------------------------------------------------------
    // Getters y Setters
    // ---------------------------------------------------------

    public String getUuidMedicamento() {
        return uuidMedicamento;
    }

    public void setUuidMedicamento(String uuidMedicamento) {
        this.uuidMedicamento = uuidMedicamento;
    }

    public String getDescripcionMedicamento() {
        return descripcionMedicamento;
    }

    public void setDescripcionMedicamento(String descripcionMedicamento) {
        this.descripcionMedicamento = descripcionMedicamento;
    }

    public String getUuidPresentacion() {
        return uuidPresentacion;
    }

    public void setUuidPresentacion(String uuidPresentacion) {
        this.uuidPresentacion = uuidPresentacion;
    }

    public String getDescripcionPresentacion() {
        return descripcionPresentacion;
    }

    public void setDescripcionPresentacion(String descripcionPresentacion) {
        this.descripcionPresentacion = descripcionPresentacion;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getMaximoPorEntrega() {
        return maximoPorEntrega;
    }

    public void setMaximoPorEntrega(Integer maximoPorEntrega) {
        this.maximoPorEntrega = maximoPorEntrega;
    }

    public boolean isRequiereTratamiento() {
        return requiereTratamiento;
    }

    public void setRequiereTratamiento(boolean requiereTratamiento) {
        this.requiereTratamiento = requiereTratamiento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ---------------------------------------------------------

    @Override
    public String toString() {
        String nombre = descripcionMedicamento != null ? descripcionMedicamento : "—";
        String presentacion = descripcionPresentacion != null ? descripcionPresentacion : "—";
        return nombre + " (" + presentacion + ")";
    }
}