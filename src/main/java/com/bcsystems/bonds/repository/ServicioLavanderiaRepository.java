package com.bcsystems.bonds.repository;

import com.bcsystems.bonds.domain.ServicioLavanderia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioLavanderiaRepository extends JpaRepository<ServicioLavanderia, Integer> {
    List<ServicioLavanderia> findAllByOrderByNombreAsc();
    List<ServicioLavanderia> findByActivoTrueOrderByNombreAsc();
}
