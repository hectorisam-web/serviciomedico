package mintur.serviciomedico.model;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Modelo para la tabla control_diario.
 * Se mantiene java.sql.Time para compatibilidad directa con el Driver JDBC y LocalTime de ser necesario.
 */
public class ControlDiario {

    private String uuidControl;
    private String uuidTitular;
    private String uuidTitularFam;
    
    // CORRECCIÓN: Coloca las anotaciones aquí, sobre las variables originales
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaControl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime horaControl;
    
    private BigDecimal pesoKg;
    private Integer tensionSistolica;
    private Integer tensionDiastolica;
    private Integer frecuencia_cardiaca;
    private Integer spo2;
    private BigDecimal temperatura;

    private String uuidMedico;
    private String uuidConsultorio;

    private String motivoConsulta;
    private String diagnostico;
    private String indicaciones;
    private String observaciones;

    private UUID usuarioRegistro;
    private Boolean status;

    private BigDecimal tallaCm;
    private Long secuencia;

    // Campos derivados para UI (JOINs)
    private String paciente;
    private String nombreMedico;

    // ============================
    // GETTERS Y SETTERS
    // ============================

    public String getUuidControl() { return uuidControl; }
    public void setUuidControl(String uuidControl) { this.uuidControl = uuidControl; }

    public String getUuidTitular() { return uuidTitular; }
    public void setUuidTitular(String uuidTitular) { this.uuidTitular = uuidTitular; }

    public String getUuidTitularFam() { return uuidTitularFam; }
    public void setUuidTitularFam(String uuidTitularFam) { this.uuidTitularFam = uuidTitularFam; }

    public LocalDate getFechaControl() { return fechaControl; }
    public void setFechaControl(LocalDate fechaControl) { this.fechaControl = fechaControl; }

    public LocalTime getHoraControl() { return horaControl; }
    public void setHoraControl(LocalTime horaControl) { this.horaControl = horaControl; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public Integer getTensionSistolica() { return tensionSistolica; }
    public void setTensionSistolica(Integer tensionSistolica) { this.tensionSistolica = tensionSistolica; }

    public Integer getTensionDiastolica() { return tensionDiastolica; }
    public void setTensionDiastolica(Integer tensionDiastolica) { this.tensionDiastolica = tensionDiastolica; }
    
    public Integer getFrecuenciaCardiaca() { return frecuencia_cardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuencia_cardiaca) { this.frecuencia_cardiaca = frecuencia_cardiaca; }

    public Integer getSaturacionOxigeno() { return spo2; }
    public void setSaturacionOxigeno(Integer spo2) { this.spo2 = spo2; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public String getUuidMedico() { return uuidMedico; }
    public void setUuidMedico(String uuidMedico) { this.uuidMedico = uuidMedico; }

    public String getUuidConsultorio() { return uuidConsultorio; }
    public void setUuidConsultorio(String uuidConsultorio) { this.uuidConsultorio = uuidConsultorio; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getIndicaciones() { return indicaciones; }
    public void setIndicaciones(String indicaciones) { this.indicaciones = indicaciones; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public UUID getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(UUID usuarioRegistro) { this.usuarioRegistro = usuarioRegistro; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public BigDecimal getTallaCm() { return tallaCm; }
    public void setTallaCm(BigDecimal tallaCm) { this.tallaCm = tallaCm; }

    public Long getSecuencia() { return secuencia; }
    public void setSecuencia(Long secuencia) { this.secuencia = secuencia; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String paciente) { this.paciente = paciente; }

    public String getNombreMedico() { return nombreMedico; }
    public void setNombreMedico(String nombreMedico) { this.nombreMedico = nombreMedico; }

    // Helper para lógica booleana
    public boolean isStatus() {
        return status != null && status;
    }

    @Override
    public String toString() {
        return "ControlDiario{" +
                "secuencia=" + secuencia +
                ", paciente='" + paciente + '\'' +
                ", medico='" + nombreMedico + '\'' +
                ", motivo='" + motivoConsulta + '\'' +
                '}';
    }
    
    
}