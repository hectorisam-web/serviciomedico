package mintur.serviciomedico.service.impl;

import mintur.serviciomedico.service.BitacoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class BitacoraServiceImpl implements BitacoraService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

  @Override
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void registrar(UUID usuarioId, String modulo, String accion, 
                      String anterior, String nuevo, String ip, 
                      String desc, UUID idEntidad, String tabla) {
    
    // FILTRO DE DESARROLLO: Evitar basura en la bitácora
    // Si la acción es UPDATE y no hubo cambios reales, no guardamos nada.
    if ("UPDATE".equals(accion) && anterior != null && anterior.equals(nuevo)) {
        return; 
    }

    String sql = """
        INSERT INTO public.bitacora_servicom 
        (usuario_id, modulo, accion, valor_anterior, valor_nuevo, 
         ip_terminal, descripcion, entidad_uuid, tabla_afectada)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
    jdbcTemplate.update(sql, 
        usuarioId, modulo, accion, anterior, nuevo, 
        ip, desc, idEntidad, tabla
    );
}
}