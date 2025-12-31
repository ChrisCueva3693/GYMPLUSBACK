package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.auth.LoginRequest;
import com.gymplus.backend.dto.auth.LoginResponse;
import com.gymplus.backend.dto.auth.RegisterRequest;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Rol;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.entity.UsuarioRol;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.RolRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.AuthService;
import com.gymplus.backend.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final String ROL_CLIENTE = "CLIENTE";

    private final UsuarioRepository usuarioRepository;
    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public UsuarioResponseDto register(RegisterRequest request) {
        validarUnicidad(request.getEmail(), request.getUsername());

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setUsername(request.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setGimnasio(obtenerGimnasio(request.getIdGimnasio()));
        if (request.getIdSucursalPorDefecto() != null) {
            usuario.setSucursalPorDefecto(obtenerSucursal(request.getIdSucursalPorDefecto()));
        }
        usuario.setUsuarioRoles(new HashSet<>());
        Rol rolCliente = obtenerRolPorNombre(ROL_CLIENTE);
        usuario.getUsuarioRoles().add(UsuarioRol.builder().usuario(usuario).rol(rolCliente).build());

        usuarioRepository.save(usuario);
        return toDto(usuario);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Set<String> roles = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.toSet());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("roles", roles);

        String token = jwtUtil.generateToken(claims, userDetails);

        return LoginResponse.builder()
                .userId(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .token(token)
                .roles(roles)
                .gimnasioId(usuario.getGimnasio().getId())
                .gimnasioNombre(usuario.getGimnasio().getNombre())
                .sucursalId(usuario.getSucursalPorDefecto() != null ? usuario.getSucursalPorDefecto().getId() : null)
                .sucursalNombre(
                        usuario.getSucursalPorDefecto() != null ? usuario.getSucursalPorDefecto().getNombre() : null)
                .build();
    }

    private void validarUnicidad(String email, String username) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado");
        }
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private Rol obtenerRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el rol " + nombre));
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
