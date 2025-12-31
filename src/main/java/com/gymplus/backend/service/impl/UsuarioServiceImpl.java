package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.usuario.UsuarioCreateUpdateDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.dto.usuario.VincularSucursalDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Rol;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.entity.UsuarioRol;
import com.gymplus.backend.entity.UsuarioSucursal;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.RolRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.repository.UsuarioSucursalRepository;
import com.gymplus.backend.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioSucursalRepository usuarioSucursalRepository;
    private final RolRepository rolRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public UsuarioResponseDto crear(UsuarioCreateUpdateDto dto) {
        validarUnicidad(dto.getEmail(), dto.getUsername(), null);
        Usuario usuario = new Usuario();
        modelMapper.map(dto, usuario);
        Gimnasio gimnasio = obtenerGimnasio(dto.getIdGimnasio());
        usuario.setGimnasio(gimnasio);
        if (dto.getIdSucursalPorDefecto() != null) {
            usuario.setSucursalPorDefecto(obtenerSucursal(dto.getIdSucursalPorDefecto()));
        }
        if (dto.getPassword() != null) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        usuario.setUsuarioRoles(new HashSet<>());
        asignarRoles(usuario, dto.getRoles());
        usuarioRepository.save(usuario);
        return toDto(usuario);
    }

    @Override
    public UsuarioResponseDto actualizar(Long id, UsuarioCreateUpdateDto dto) {
        Usuario usuario = obtenerEntidad(id);
        validarUnicidad(dto.getEmail(), dto.getUsername(), usuario);
        Gimnasio gimnasio = dto.getIdGimnasio() != null ? obtenerGimnasio(dto.getIdGimnasio()) : usuario.getGimnasio();
        usuario.setGimnasio(gimnasio);
        if (dto.getIdSucursalPorDefecto() != null) {
            usuario.setSucursalPorDefecto(obtenerSucursal(dto.getIdSucursalPorDefecto()));
        }
        modelMapper.map(dto, usuario);
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        asignarRoles(usuario, dto.getRoles());
        return toDto(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = obtenerEntidad(id);
        usuarioRepository.delete(usuario);
    }

    @Override
    public void cambiarRol(Long id, Set<String> roles) {
        Usuario usuario = obtenerEntidad(id);
        asignarRoles(usuario, roles);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPorCedula(String cedula) {
        Usuario usuario = usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con cédula " + cedula + " no encontrado"));
        return toDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarPorGimnasio(Long gimnasioId) {
        return usuarioRepository.findByGimnasioId(gimnasioId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarPorSucursal(Long sucursalId) {
        return usuarioRepository.findBySucursalPorDefectoId(sucursalId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void vincularSucursal(Long idUsuario, VincularSucursalDto dto) {
        Usuario usuario = obtenerEntidad(idUsuario);
        Sucursal nuevaSucursal = obtenerSucursal(dto.getIdSucursal());

        // Verificar que la sucursal pertenece al mismo gimnasio
        if (!usuario.getGimnasio().getId().equals(nuevaSucursal.getGimnasio().getId())) {
            throw new IllegalArgumentException("La sucursal destino debe pertenecer al mismo gimnasio del usuario");
        }

        // Marcar vinculaciones anteriores como inactivas (opcional, o mantener
        // historial activo)
        // En este caso, permitimos múltiples activas, pero actualizamos su sucursal por
        // defecto

        // Crear nueva vinculación
        UsuarioSucursal vinculacion = UsuarioSucursal.builder()
                .usuario(usuario)
                .sucursal(nuevaSucursal)
                .fechaVinculacion(LocalDateTime.now())
                .activo(true)
                .notas(dto.getNotas())
                .build();

        // Guardar vinculación (necesitamos el repo, si no lo tenemos inyectado,
        // añadirlo)
        // Como no tenemos UsuarioSucursalRepository inyectado aun, lo añadimos.
        // Espera, check constructor arguments...
        // Mejor añadir el repo a la clase.

        // Actualizar sucursal por defecto
        usuario.setSucursalPorDefecto(nuevaSucursal);
        usuarioRepository.save(usuario);

        // Guardar vinculación
        usuarioSucursalRepository.save(vinculacion);
    }

    private void validarUnicidad(String email, String username, Usuario actual) {
        if (email != null) {
            boolean emailExiste = usuarioRepository.existsByEmail(email);
            if (emailExiste && (actual == null || !email.equalsIgnoreCase(actual.getEmail()))) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }
        if (username != null) {
            boolean usernameExiste = usuarioRepository.existsByUsername(username);
            if (usernameExiste && (actual == null || !username.equalsIgnoreCase(actual.getUsername()))) {
                throw new IllegalArgumentException("El usuario ya está registrado");
            }
        }
    }

    private Usuario obtenerEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private void asignarRoles(Usuario usuario, Set<String> roles) {
        usuario.getUsuarioRoles().clear();
        if (roles == null || roles.isEmpty()) {
            return;
        }
        Set<String> rolesNormalizados = roles.stream()
                .filter(nombre -> nombre != null && !nombre.isBlank())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        Set<Rol> rolesEncontrados = rolRepository.findByNombreIn(rolesNormalizados);
        if (rolesEncontrados.size() != rolesNormalizados.size()) {
            throw new EntityNotFoundException("Alguno de los roles proporcionados no existe");
        }
        rolesEncontrados.forEach(rol -> usuario.getUsuarioRoles().add(UsuarioRol.builder()
                .usuario(usuario)
                .rol(rol)
                .build()));
    }

    private UsuarioResponseDto toDto(Usuario usuario) {
        UsuarioResponseDto dto = modelMapper.map(usuario, UsuarioResponseDto.class);
        dto.setIdGimnasio(usuario.getGimnasio().getId());
        dto.setIdSucursalPorDefecto(
                usuario.getSucursalPorDefecto() != null ? usuario.getSucursalPorDefecto().getId() : null);
        Set<String> roles = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        return dto;
    }
}
