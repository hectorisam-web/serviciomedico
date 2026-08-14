package mintur.serviciomedico.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public class Usuario {
    private String uuidUsuario;
    private String usuario;
    
    @JsonIgnore // Evita que la contraseña se envíe en la respuesta JSON al Frontend
    private String pass;
    
    private String nombres;
    private String apellidos;
    private String uuidRol;
    private String descripcionRol; // <--- Este campo hará que el Dashboard sea dinámico
    private boolean estatus;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion; // Agregado para coincidir con tu SQL

    // Constructor vacío (Requerido para frameworks)
    public Usuario() {}

    // Constructor para Login
    public Usuario(String usuario, String pass) {
        this.usuario = usuario;
        this.pass = pass;
    }

    // Getters y Setters
    public String getUuidUsuario() { return uuidUsuario; }
    public void setUuidUsuario(String uuidUsuario) { this.uuidUsuario = uuidUsuario; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getUuidRol() { return uuidRol; }
    public void setUuidRol(String uuidRol) { this.uuidRol = uuidRol; }

    public String getDescripcionRol() { return descripcionRol; }
    public void setDescripcionRol(String descripcionRol) { this.descripcionRol = descripcionRol; }

    public boolean isEstatus() { return estatus; }
    public void setEstatus(boolean estatus) { this.estatus = estatus; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    @Override
    public String toString() {
        return "Usuario{" +
                "uuidUsuario='" + uuidUsuario + '\'' +
                ", usuario='" + usuario + '\'' +
                ", descripcionRol='" + descripcionRol + '\'' +
                '}';
    }
}