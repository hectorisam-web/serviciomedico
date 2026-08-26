package mintur.serviciomedico.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class Cita {
    private UUID uuidCita;
    private UUID uuidTitular;
    private UUID uuidTitularFamiliar;
    private UUID uuidConsultorio;
    private UUID uuidMedico;
    private LocalDate fecha;
    private LocalDate fechaCita;
    private LocalTime hora;
    private String observaciones;
    private String estado;
    private String nombreMedico;
    private String nombreConsultorio;

    // Getters y Setters
    public UUID getUuidCita() { return uuidCita; }
    public void setUuidCita(UUID uuidCita) { this.uuidCita = uuidCita; }

    public UUID getUuidTitular() { return uuidTitular; }
    public void setUuidTitular(UUID uuidTitular) { this.uuidTitular = uuidTitular; }

    public UUID getUuidTitularFamiliar() { return uuidTitularFamiliar; }
    public void setUuidTitularFamiliar(UUID uuidTitularFamiliar) { this.uuidTitularFamiliar = uuidTitularFamiliar; }

    public UUID getUuidConsultorio() { return uuidConsultorio; }
    public void setUuidConsultorio(UUID uuidConsultorio) { this.uuidConsultorio = uuidConsultorio; }

    public UUID getUuidMedico() { return uuidMedico; }
    public void setUuidMedico(UUID uuidMedico) { this.uuidMedico = uuidMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalDate getFechaCita() { return fechaCita; }
    public void setFechaCita(LocalDate fechaCita) { this.fechaCita = fechaCita; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getNombreMedico() {
    return nombreMedico;
}

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getNombreConsultorio() {
        return nombreConsultorio;
    }

    public void setNombreConsultorio(String nombreConsultorio) {
        this.nombreConsultorio = nombreConsultorio;
    }
}