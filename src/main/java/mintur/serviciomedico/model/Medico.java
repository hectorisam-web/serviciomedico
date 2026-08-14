package mintur.serviciomedico.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Medico {

    @JsonProperty("uuidMedico")
    private String uuidMedico;
    
    @JsonProperty("uuidEspecialidad")
    private String uuidEspecialidad;
    
    @JsonProperty("cedulaMedico")
    private int cedulaMedico;
    
    @JsonProperty("nombresMedico")
    private String nombresMedico;
    
    @JsonProperty("apellidosMedico")
    private String apellidosMedico;
    
    @JsonProperty("telefonoMedico")
    private String telefonoMedico;
    
    @JsonProperty("emailMedico")
    private String emailMedico;
    
    @JsonProperty("status")
    private boolean status;
    
    @JsonProperty("uuidConsultorio")
    private String uuidConsultorio;
    
    @JsonProperty("mpps")
    private String mpps; 
    
    @JsonProperty("descripcionEspecialidad")
    private String descripcionEspecialidad; 

    public Medico() {
    }

    // --- GETTERS Y SETTERS ---
    public String getUuidMedico() { return uuidMedico; }
    public void setUuidMedico(String uuidMedico) { this.uuidMedico = uuidMedico; }

    public String getUuidEspecialidad() { return uuidEspecialidad; }
    public void setUuidEspecialidad(String uuidEspecialidad) { this.uuidEspecialidad = uuidEspecialidad; }

    public int getCedulaMedico() { return cedulaMedico; }
    public void setCedulaMedico(int cedulaMedico) { this.cedulaMedico = cedulaMedico; }

    public String getNombresMedico() { return nombresMedico; }
    public void setNombresMedico(String nombresMedico) { this.nombresMedico = nombresMedico; }

    public String getApellidosMedico() { return apellidosMedico; }
    public void setApellidosMedico(String apellidosMedico) { this.apellidosMedico = apellidosMedico; }

    public String getTelefonoMedico() { return telefonoMedico; }
    public void setTelefonoMedico(String telefonoMedico) { this.telefonoMedico = telefonoMedico; }

    public String getEmailMedico() { return emailMedico; }
    public void setEmailMedico(String emailMedico) { this.emailMedico = emailMedico; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public String getUuidConsultorio() { return uuidConsultorio; }
    public void setUuidConsultorio(String uuidConsultorio) { this.uuidConsultorio = uuidConsultorio; }

    public String getMpps() { return mpps; }
    public void setMpps(String mpps) { this.mpps = mpps; }

    public String getDescripcionEspecialidad() { return descripcionEspecialidad; }
    public void setDescripcionEspecialidad(String descripcionEspecialidad) { this.descripcionEspecialidad = descripcionEspecialidad; }

    // --- MÉTODOS DE CONVENIENCIA (LOS QUE REVISAMOS) ---

    @Override
    public String toString() {
        if (uuidMedico == null || uuidMedico.isEmpty()) {
            return "NUEVO REGISTRO / TODOS";
        }
        String nombreMostrable = getNombreCompleto().isEmpty() ? "SIN NOMBRE" : getNombreCompleto();
        String mppsMostrable = (mpps != null) ? mpps : "S/N";
        
        return getTratamiento() + " " + nombreMostrable + " (MPPS: " + mppsMostrable + ")";
    }

    public String getNombreCompleto() {
        String ape = (apellidosMedico != null) ? apellidosMedico.trim() : "";
        String nom = (nombresMedico != null) ? nombresMedico.trim() : "";
        return (ape + " " + nom).trim();
    }

    public String getTratamiento() {
        return "Dr(a).";
    }
}