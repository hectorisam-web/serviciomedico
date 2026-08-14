// PermisosDTO.java
package mintur.serviciomedico.model;
import java.util.List;
import java.util.Map;

public class PermisosDTO {
    public List<Map<String, String>> disponibles;
    public List<String> asignados;

    public PermisosDTO(List<Map<String, String>> disponibles, List<String> asignados) {
        this.disponibles = disponibles;
        this.asignados = asignados;
    }
}