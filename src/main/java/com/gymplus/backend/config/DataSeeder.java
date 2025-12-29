package com.gymplus.backend.config;

import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Rol;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.RolRepository;
import com.gymplus.backend.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final RolRepository rolRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed Rol
        if (rolRepository.findByNombre("CLIENTE").isEmpty()) {
            Rol rol = Rol.builder()
                    .nombre("CLIENTE")
                    .descripcion("Rol para clientes del gimnasio")
                    .build();
            rolRepository.save(rol);
            System.out.println("Seeded Rol: CLIENTE");
        }

        if (rolRepository.findByNombre("ADMIN").isEmpty()) {
            Rol rol = Rol.builder()
                    .nombre("ADMIN")
                    .descripcion("Rol para administradores")
                    .build();
            rolRepository.save(rol);
            System.out.println("Seeded Rol: ADMIN");
        }

        if (rolRepository.findByNombre("DEV").isEmpty()) {
            Rol rol = Rol.builder()
                    .nombre("DEV")
                    .descripcion("Rol para desarrolladores")
                    .build();
            rolRepository.save(rol);
            System.out.println("Seeded Rol: DEV");
        }

        // Seed Gimnasio
        Gimnasio gimnasio = null;
        if (gimnasioRepository.count() == 0) {
            gimnasio = Gimnasio.builder()
                    .nombre("GymPlus Default")
                    .ruc("1234567890001")
                    .emailContacto("contact@gymplus.com")
                    .telefono("0999999999")
                    .activo(true)
                    .build();
            gimnasio = gimnasioRepository.save(gimnasio);
            System.out.println("Seeded Gimnasio: " + gimnasio.getNombre());
        } else {
            gimnasio = gimnasioRepository.findAll().get(0);
        }

        // Seed Sucursal
        if (sucursalRepository.count() == 0 && gimnasio != null) {
            Sucursal sucursal = Sucursal.builder()
                    .gimnasio(gimnasio)
                    .nombre("Sucursal Central")
                    .ciudad("Quito")
                    .direccion("Av. Amazonas y Naciones Unidas")
                    .telefono("022222222")
                    .activo(true)
                    .build();
            sucursal = sucursalRepository.save(sucursal);
            System.out.println("Seeded Sucursal: " + sucursal.getNombre());
        }
    }
}
