package mintur.serviciomedico.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class TitularFamiliar {

    private String uuidTitularFamiliar;
    private String uuidTitular;

    private Integer cedulaTitular;
    private Integer cedulaFamiliar;

    private String apellidosFamiliar;
    private String nombresFamiliar;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaNacFamiliar;

    private String uuidParentesco;
    private String uuidSexo;

    private String numeroHistoria;
    private boolean status;

    // Campos derivados (JOIN con maestro)
    private String parentescoDescripcion;
    private String sexoDescripcion;

    // ============================
    // GETTERS & SETTERS
    // ============================

    public String getUuidTitularFamiliar() {
        return uuidTitularFamiliar;
    }

    public void setUuidTitularFamiliar(String uuidTitularFamiliar) {
        this.uuidTitularFamiliar = uuidTitularFamiliar;
    }

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

    public Integer getCedulaFamiliar() {
        return cedulaFamiliar;
    }

    public void setCedulaFamiliar(Integer cedulaFamiliar) {
        this.cedulaFamiliar = cedulaFamiliar;
    }

    public String getApellidosFamiliar() {
        return apellidosFamiliar;
    }

    public void setApellidosFamiliar(String apellidosFamiliar) {
        this.apellidosFamiliar = apellidosFamiliar;
    }

    public String getNombresFamiliar() {
        return nombresFamiliar;
    }

    public void setNombresFamiliar(String nombresFamiliar) {
        this.nombresFamiliar = nombresFamiliar;
    }

    public LocalDate getFechaNacFamiliar() {
        return fechaNacFamiliar;
    }

    public void setFechaNacFamiliar(LocalDate fechaNacFamiliar) {
        this.fechaNacFamiliar = fechaNacFamiliar;
    }

    public String getUuidParentesco() {
        return uuidParentesco;
    }

    public void setUuidParentesco(String uuidParentesco) {
        this.uuidParentesco = uuidParentesco;
    }

    public String getUuidSexo() {
        return uuidSexo;
    }

    public void setUuidSexo(String uuidSexo) {
        this.uuidSexo = uuidSexo;
    }

    public String getNumeroHistoria() {
        return numeroHistoria;
    }

    public void setNumeroHistoria(String numeroHistoria) {
        this.numeroHistoria = numeroHistoria;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getParentescoDescripcion() {
        return parentescoDescripcion;
    }

    public void setParentescoDescripcion(String parentescoDescripcion) {
        this.parentescoDescripcion = parentescoDescripcion;
    }

    public String getSexoDescripcion() {
        return sexoDescripcion;
    }

    public void setSexoDescripcion(String sexoDescripcion) {
        this.sexoDescripcion = sexoDescripcion;
    }

    // ==========================================================
    // TOSTRING PARA BITÁCORA (Mecánico para auditoría)
    // ==========================================================
    @Override
    public String toString() {
        return "Familiar{" +
                "cedula=" + cedulaFamiliar +
                ", nombre='" + nombresFamiliar + " " + apellidosFamiliar + '\'' +
                ", parentesco='" + parentescoDescripcion + '\'' +
                ", historia='" + numeroHistoria + '\'' +
                ", status=" + status +
                '}';
    }
}