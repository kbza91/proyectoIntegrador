package com.proyectoIntegrador.utnfrsr.repository;

import com.proyectoIntegrador.utnfrsr.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
