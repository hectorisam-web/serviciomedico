package mintur.serviciomedico.service;

import java.util.UUID;

public interface BitacoraService {
    // Versión extendida para auditoría profesional
    void registrar(
        UUID usuarioId, 
        String modulo, 
        String accion, 
        String anterior, 
        String nuevo, 
        String ip, 
        String desc,
        UUID idEntidad, // <--- El UUID del registro (Médico, Titular, etc.)
        String tabla    // <--- El nombre de la tabla (TITULAR, MEDICOS, etc.)
    );
}