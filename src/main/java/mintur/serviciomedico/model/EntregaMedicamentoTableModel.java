package mintur.serviciomedico.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Versión corregida: Eliminamos la dependencia del Service para evitar errores
 * de inyección y bajo rendimiento.
 */
public class EntregaMedicamentoTableModel extends AbstractTableModel {

    private final String[] columnas = {
        "Medicamento",
        "Lote",
        "Tratamiento",
        "Cant. entregada",
        "Stock disp.",
        "Nueva entrega"
    };

    private final List<ControlDiarioMedicamento> lista;

    // Constructor limpio: Solo recibe la data
    public EntregaMedicamentoTableModel(List<ControlDiarioMedicamento> lista) {
        this.lista = lista;
    }

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 3, 4, 5 -> Integer.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ControlDiarioMedicamento c = lista.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> c.getDescripcionMedicamento();
            case 1 -> c.getNumeroLote();
            case 2 -> c.getTratamiento();
            case 3 -> c.getCantidad();
            case 4 -> c.getStockDisponibleActual(); // Ahora lo lee directamente del objeto
            case 5 -> c.getNuevaEntrega();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 5;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 5 && value instanceof Integer val) {
            ControlDiarioMedicamento c = lista.get(row);
            
            // Validación: No permitir negativos ni exceder stock cargado
            int cantidad = Math.max(0, val);
            if (cantidad > c.getStockDisponibleActual()) {
                c.setNuevaEntrega(c.getStockDisponibleActual());
            } else {
                c.setNuevaEntrega(cantidad);
            }
            
            fireTableCellUpdated(row, col);
        }
    }

    public ControlDiarioMedicamento getItemAt(int row) {
        return (row >= 0 && row < lista.size()) ? lista.get(row) : null;
    }
}