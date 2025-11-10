package com.proyectoIntegrador.utnfrsr.services;


import com.proyectoIntegrador.utnfrsr.models.Producto;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {

    @Transactional
    void create(
            String nombre,
            String marca,
            Double precio,
            Integer stock,
            Integer wattsConsumo,
            String descripcion,
            String arquitectura,
            String categoriaNombre,
            List<MultipartFile> imagenes);

    @Transactional
    void update(
            int id,
            String nombre,
            String marca,
            Double precio,
            Integer stock,
            Integer wattsConsumo,
            String descripcion,
            String arquitectura,
            String categoriaNombre,
            List<MultipartFile> imagenes);

    void delete(Producto producto);

    Producto read(int id);

    List<Producto> listProductos();
}
