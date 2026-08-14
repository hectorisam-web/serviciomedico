package mintur.serviciomedico.model;

import java.time.LocalDate;

public class LoteMedicamento {

    private String uuidLote;
    private String descripcionLote;
    private String numeroLote;
    private int stockInicial;
    private int stockDisponible;
    private LocalDate fechaVencimiento;
    private boolean activo;
    private String uuidMedicamento;

    public LoteMedicamento() {}

    public String getUuidLote() { return uuidLote; }
    public void setUuidLote(String uuidLote) { this.uuidLote = uuidLote; }

    public String getDescripcionLote() { return descripcionLote; }
    public void setDescripcionLote(String descripcionLote) { this.descripcionLote = descripcionLote; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }

    public int getStockInicial() { return stockInicial; }
    public void setStockInicial(int stockInicial) { this.stockInicial = stockInicial; }

    public int getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getUuidMedicamento() { return uuidMedicamento; }
    public void setUuidMedicamento(String uuidMedicamento) { this.uuidMedicamento = uuidMedicamento; }

    @Override
    public String toString() {
        String lote = (numeroLote != null && !numeroLote.isBlank()) ? numeroLote : "Sin lote";
        return lote + " (Stock: " + stockDisponible + ")";
    }
}