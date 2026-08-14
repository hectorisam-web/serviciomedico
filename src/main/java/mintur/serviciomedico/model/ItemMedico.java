package mintur.serviciomedico.model;

public class ItemMedico {

    private final String uuid;
    private final String nombre;

    public ItemMedico(String uuid, String nombre) {
        this.uuid = uuid;
        this.nombre = nombre;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
