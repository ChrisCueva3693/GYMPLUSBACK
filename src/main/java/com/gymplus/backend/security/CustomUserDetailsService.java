package com.gymplus.backend.security;

import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UsuarioRepository usuarioRepository;

        @Override
        @org.springframework.transaction.annotation.Transactional(readOnly = true)
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
                Set<GrantedAuthority> authorities = usuario.getUsuarioRoles().stream()
                                .map(usuarioRol -> new SimpleGrantedAuthority(
                                                "ROLE_" + usuarioRol.getRol().getNombre()))
                                .collect(Collectors.toSet());

                return User.builder()
                                .username(usuario.getUsername())
                                .password(usuario.getPasswordHash())
                                .authorities(authorities)
                                .accountExpired(false)
                                .accountLocked(!Boolean.TRUE.equals(usuario.getActivo()))
                                .credentialsExpired(false)
                                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                                .build();
        }
}
