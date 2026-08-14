package mintur.serviciomedico.model;

import java.time.LocalDate;

public class Titular {

    private String uuidTitular;
    private Integer cedulaTitular;
    private String apellidosTitular;
    private String nombreTitular;
    private LocalDate fechaNacimiento;
    private String uuidOrganismo;
    private String uuidSexo;
    private String nroHistoria;
    private boolean status;
    private String telefono;

    // Campos adicionales para la presentación (Resultados del JOIN)
    private String nombreOrganismo;
    private String nombreSexo;

    // ============================
    // GETTERS & SETTERS
    // ============================

    public String getUuidTitular() {
        return uuidTitular;
    }

    public void setUuidTitular(String uuidTitular) {
        this.uuidTitular = uuidTitular;
    }

    public Integer getCedulaTitular() {
        return cedulaTitular;
    }

    public void setCedulaTitular(Integer cedulaTitular) {
        this.cedulaTitular = cedulaTitular;
    }

    public String getApellidosTitular() {
        return apellidosTitular;
    }

    public void setApellidosTitular(String apellidosTitular) {
        this.apellidosTitular = apellidosTitular;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getUuidOrganismo() {
        return uuidOrganismo;
    }

    public void setUuidOrganismo(String uuidOrganismo) {
        this.uuidOrganismo = uuidOrganismo;
    }

    public String getUuidSexo() {
        return uuidSexo;
    }

    public void setUuidSexo(String uuidSexo) {
        this.uuidSexo = uuidSexo;
    }

    public String getNroHistoria() {
        return nroHistoria;
    }

    public void setNroHistoria(String nroHistoria) {
        this.nroHistoria = nroHistoria;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    // Getters y Setters para los nuevos campos de presentación
    
    public String getNombreOrganismo() {
        return nombreOrganismo;
    }

    public void setNombreOrganismo(String nombreOrganismo) {
        this.nombreOrganismo = nombreOrganismo;
    }

    public String getNombreSexo() {
        return nombreSexo;
    }

    public void setNombreSexo(String nombreSexo) {
        this.nombreSexo = nombreSexo;
    }
}