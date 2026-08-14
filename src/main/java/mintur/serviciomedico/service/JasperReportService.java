package mintur.serviciomedico.service;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.Map;

@Service
public class JasperReportService {

    @Autowired
    private DataSource dataSource;

    public byte[] generarPdf(String nombreReporte, Map<String, Object> parametros) throws Exception {
        InputStream reportStream = getClass().getResourceAsStream("/reports/" + nombreReporte + ".jasper");
        
        if (reportStream == null) {
            throw new Exception("Archivo .jasper no encontrado en /reports/" + nombreReporte + ".jasper");
        }

        // Carga el logo del classpath y lo mapea a un archivo temporal del sistema operativo
        try (InputStream logoStream = getClass().getResourceAsStream("/icons/Logo_Turismo.png")) {
            if (logoStream == null) {
                throw new Exception("Logo no encontrado en /icons/Logo_Turismo.png");
            }
            
            File tempLogo = File.createTempFile("logo_turismo", ".png");
            Files.copy(logoStream, tempLogo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempLogo.deleteOnExit();
            
            // Le pasamos la ruta absoluta en texto limpio (funciona en cualquier servidor y Windows/Linux)
            parametros.put("LOGO_PATH", tempLogo.getAbsolutePath());
        }

        try (Connection conn = dataSource.getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parametros, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}