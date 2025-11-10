package com.proyectoIntegrador.utnfrsr.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String guardarArchivo(MultipartFile file);

    String guardarArchivoProducto(MultipartFile file);

    String guardarArchivoCategoria(MultipartFile file);

    Resource cargarArchivo(String filename);
}
