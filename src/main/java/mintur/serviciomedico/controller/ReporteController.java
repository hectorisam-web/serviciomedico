package mintur.serviciomedico.controller;

import java.io.InputStream;
import mintur.serviciomedico.service.JasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "http://localhost:5173") 
public class ReporteController {

    private static final Logger logger = Logger.getLogger(ReporteController.class.getName());

    @Autowired
    private JasperReportService reportService;
    
    private byte[] cargarBytesIcono(String ruta) {
    try {
        ClassPathResource resource = new ClassPathResource(ruta);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                return org.springframework.util.StreamUtils.copyToByteArray(inputStream);
            }
        } else {
            logger.warning("Ícono no encontrado en classpath: " + ruta);
        }
    } catch (Exception e) {
        logger.warning("Error leyendo el ícono " + ruta + ": " + e.getMessage());
    }
    return null;
}
    
    /*
     * Endpoint genérico para descargar cualquier reporte en PDF.
     */
    @GetMapping("/descargar/{nombreReporte}")
    public ResponseEntity<byte[]> descargarReporte(
        @PathVariable String nombreReporte,
        @RequestParam Map<String, String> allParams) {
    
    try {
        logger.info("Generando reporte: " + nombreReporte + " con parámetros: " + allParams);
        
        // Convertimos a un mapa de objetos para permitir tipos como Date
        Map<String, Object> jasperParams = new HashMap<>(allParams);

        // --- 1. PROCESAMIENTO DE FECHAS ---
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().toLowerCase().contains("fecha")) {
                try {
                    java.sql.Date fechaSql = new java.sql.Date(sdf.parse(entry.getValue()).getTime());
                    jasperParams.put(entry.getKey(), fechaSql);
                } catch (Exception e) {
                    logger.warning("No se pudo convertir el parámetro de fecha: " + entry.getKey());
                }
            }
        }

        // --- 2. PROCESAMIENTO DE MÉDICO ---
        String uuidMedico = allParams.get("uuidMedico");
        String nombreMedico = "General / Todos";

        if (uuidMedico == null || uuidMedico.trim().isEmpty() || 
            uuidMedico.equalsIgnoreCase("null") || 
            uuidMedico.equalsIgnoreCase("undefined") || 
            uuidMedico.equalsIgnoreCase("TODOS")) {

            jasperParams.put("MEDICO", null);
            nombreMedico = "General / Todos";
        } else {
            jasperParams.put("MEDICO", uuidMedico);
            nombreMedico = "Dr. Seleccionado"; 
        }

        jasperParams.put("MEDICO_NOMBRE", nombreMedico);
        jasperParams.put("REPORT_DATE", new java.sql.Date(System.currentTimeMillis()));

        // --- 3. CARGA DE IMÁGENES DINÁMICAS (Multiplataforma y Seguras) ---
        try {
            jasperParams.put("LOGO_PATH", cargarBytesIcono("icons/Logo_Turismo.png"));
            jasperParams.put("LOGO_TURISMO", cargarBytesIcono("icons/Logo_Turismo.png"));
            jasperParams.put("LOGO_CONSULTA", cargarBytesIcono("icons/jgh_consulta.png"));

            // Opcionales / Resto de íconos
            jasperParams.put("CLINICA_IMG", cargarBytesIcono("icons/clinica.png"));
            jasperParams.put("FAMILIA_IMG", cargarBytesIcono("icons/familia.png"));
            jasperParams.put("JGH_IMG", cargarBytesIcono("icons/jgh.png"));
            jasperParams.put("LOGIN_IMG", cargarBytesIcono("icons/Login.png"));
            jasperParams.put("LOGO_200_IMG", cargarBytesIcono("icons/Logo_200.png"));
            jasperParams.put("MEDICINAS_IMG", cargarBytesIcono("icons/medicinas.png"));
            jasperParams.put("MEDICOS_IMG", cargarBytesIcono("icons/medicos.png"));
            jasperParams.put("PACIENTES_IMG", cargarBytesIcono("icons/pacientes.png"));
            jasperParams.put("USUARIOS_IMG", cargarBytesIcono("icons/usuarios.png"));

        } catch (Exception e) {
            logger.warning("No se pudieron cargar algunos íconos institucionales: " + e.getMessage());
        }
    
        // --- 4. GENERACIÓN DEL PDF ---
        byte[] reportePdf = reportService.generarPdf(nombreReporte, jasperParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String fileName = nombreReporte + "_" + System.currentTimeMillis() + ".pdf";
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(fileName).build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(reportePdf, headers, HttpStatus.OK);

    } catch (Exception e) {
        logger.severe("Error al generar el reporte " + nombreReporte + ": " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

}