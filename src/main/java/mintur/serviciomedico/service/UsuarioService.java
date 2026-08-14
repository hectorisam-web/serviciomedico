package mintur.serviciomedico.service;

import java.util.HashMap;
import mintur.serviciomedico.dao.UsuarioDao;
import mintur.serviciomedico.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;
    private final BitacoraService bitacoraService;
    
    @Autowired
    public UsuarioService(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder, BitacoraService bitacoraService) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
        this.bitacoraService = bitacoraService;
    }

    public List<String> listarPermisosAsignados(String uuidUsuario) {
        return usuarioDao.findPermisosByUsuarioId(uuidUsuario);
    }
    
 
    public List<Map<String, String>> listarTodosLosPermisos() {
        return usuarioDao.findAllPermisos();
    }
    // --- AUTENTICACIÓN ---
    public Usuario login(String username, String passwordPlano) throws Exception {
        Usuario u = usuarioDao.buscarPorUsuario(username);
        if (u == null) return null;
        if (!passwordEncoder.matches(passwordPlano, u.getPass())) return null;
        return u;
    }

    // --- CRUD ADMINISTRATIVO ---
    public List<Usuario> listarTodos() throws Exception {
        return usuarioDao.findAll();
    }

    public void registrarUsuario(Usuario u, UUID adminId, String ip) throws Exception {
        u.setPass(passwordEncoder.encode("123456")); 
        u.setEstatus(true);
        usuarioDao.insertar(u);
        bitacoraService.registrar(adminId, "USUARIOS", "INSERT", null, 
            "Usuario: " + u.getUsuario(), ip, "Alta de usuario", null, "USUARIOS");
    }

    public void actualizarUsuario(Usuario u, UUID adminId, String ip) throws Exception {
        if (u.getUuidUsuario() == null || u.getUuidUsuario().isEmpty()) {
            throw new IllegalArgumentException("El ID del usuario es requerido.");
        }
        usuarioDao.actualizar(u);
        bitacoraService.registrar(adminId, "USUARIOS", "UPDATE", "Datos previos", 
            "Actualización: " + u.getUsuario(), ip, "Modificación de usuario", 
            UUID.fromString(u.getUuidUsuario()), "USUARIOS");
    }

    public void cambiarRolUsuario(String uuidUsuario, String nuevoUuidRol, UUID adminId, String ip) throws Exception {
        usuarioDao.actualizarRol(uuidUsuario, nuevoUuidRol);
        bitacoraService.registrar(adminId, "USUARIOS", "UPDATE_ROL", null, 
            "Nuevo Rol: " + nuevoUuidRol, ip, "Cambio de privilegios", 
            UUID.fromString(uuidUsuario), "USUARIOS");
    }
    
    @Transactional
    public void guardarPermisos(String uuidUsuario, List<String> permisosSeleccionados) {
    // 1. Necesitamos el ID del ROL del usuario (o si manejas permisos directos al usuario)
    // Asumiré que quieres guardar en roles_permisos por el nombre de tu tabla
    String uuidRol = usuarioDao.obtenerUuidRolPorUsuario(uuidUsuario);
    
    // 2. Borramos los permisos actuales del rol para evitar duplicados
    usuarioDao.eliminarPermisosDelRol(uuidRol);
    
    // 3. Insertamos los nuevos permisos seleccionados
    for (String uuidPermiso : permisosSeleccionados) {
        usuarioDao.insertarPermisoEnRol(uuidRol, uuidPermiso);
    }
    
    logger.info("Permisos actualizados para el usuario: " + uuidUsuario);
}
    
    // Dentro de UsuarioService.java
public Map<String, Object> loginConPermisos(String username, String passwordPlano) throws Exception {
    Usuario u = usuarioDao.buscarPorUsuario(username);
    if (u == null || !passwordEncoder.matches(passwordPlano, u.getPass())) {
        return null;
    }
    
    // Obtenemos los permisos del rol del usuario
    List<String> permisos = listarPermisosAsignados(u.getUuidUsuario());
    
    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("usuario", u);
    respuesta.put("permisos", permisos); // <-- Enviamos la lista de una vez
    return respuesta;
}
    

}