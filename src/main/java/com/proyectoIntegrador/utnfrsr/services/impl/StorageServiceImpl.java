package com.proyectoIntegrador.utnfrsr.services.impl;

import com.proyectoIntegrador.utnfrsr.services.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path productosPath = Paths.get("uploads/productos");
    private final Path categoriasPath = Paths.get("uploads/categorias");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(productosPath);
            Files.createDirectories(categoriasPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron inicializar los directorios de subida", e);
        }
    }

    private String generarNombreArchivo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new RuntimeException("Nombre de archivo inválido");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

        if (!List.of("png", "jpg", "jpeg", "gif").contains(extension)) {
            throw new RuntimeException("Formato no permitido: " + extension);
        }

        return UUID.randomUUID() + "_" + originalFilename;
    }

    @Override
    public String guardarArchivo(MultipartFile file) {
        return guardarArchivoProducto(file);
    }

    @Override
    public String guardarArchivoProducto(MultipartFile file) {
        return guardarArchivoEnDirectorio(file, productosPath);
    }

    @Override
    public String guardarArchivoCategoria(MultipartFile file) {
        return guardarArchivoEnDirectorio(file, categoriasPath);
    }

    private String guardarArchivoEnDirectorio(MultipartFile file, Path destinoDirectorio) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }

            String nuevoNombre = generarNombreArchivo(file);
            Path destino = destinoDirectorio.resolve(nuevoNombre);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Guardando archivo en: " + destino.toAbsolutePath());
            return nuevoNombre;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }

    @Override
    public Resource cargarArchivo(String filename) {
        try {
            // Buscar en ambos directorios
            Path fileProducto = productosPath.resolve(filename);
            Path fileCategoria = categoriasPath.resolve(filename);

            Path file = Files.exists(fileProducto) ? fileProducto :
                    Files.exists(fileCategoria) ? fileCategoria : null;

            if (file == null) {
                throw new RuntimeException("Archivo no encontrado: " + filename);
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}