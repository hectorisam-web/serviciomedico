package mintur.serviciomedico.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles", schema = "public")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    private Integer idCodigo;

    @Column(name = "uuid_rol", nullable = false, unique = true)
    private String uuidRol;

    @Column(name = "descripcion", nullable = false, length = 50)
    private String descripcion;

    @Column(name = "estatus")
    private boolean estatus = true;

    // Se mantiene como @Transient al no haber sido definida la tabla de permisos
    // en este esquema inicial. Si existe otra entidad, esto cambiará a @ElementCollection
    @Transient
    private Set<String> permisos = new HashSet<>();

    public Rol() {
    }

    public Integer getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(Integer idCodigo) {
        this.idCodigo = idCodigo;
    }

    public String getUuidRol() {
        return uuidRol;
    }

    public void setUuidRol(String uuidRol) {
        this.uuidRol = uuidRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstatus() {
        return estatus;
    }

    public void setEstatus(boolean estatus) {
        this.estatus = estatus;
    }

    public Set<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<String> permisos) {
        this.permisos = permisos;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}