package com.proyectoIntegrador.utnfrsr.controllers;

import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Categoria;
import com.proyectoIntegrador.utnfrsr.models.Imagen;
import com.proyectoIntegrador.utnfrsr.models.Producto;
import com.proyectoIntegrador.utnfrsr.repository.CategoriaRepository;
import com.proyectoIntegrador.utnfrsr.repository.ImagenRepository;
import com.proyectoIntegrador.utnfrsr.services.ProductoService;
import com.proyectoIntegrador.utnfrsr.services.StorageService;
import com.proyectoIntegrador.utnfrsr.utils.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService service;
    private final ImagenRepository imagenRepository;
    private final StorageService storageService;
    private final CategoriaRepository categoriaRepository;

    public ProductoController(ProductoService service, ImagenRepository imagenRepository, StorageService storageService, CategoriaRepository categoriaRepository) {
        this.service = service;
        this.imagenRepository = imagenRepository;
        this.storageService = storageService;
        this.categoriaRepository = categoriaRepository;
    }



    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse> createProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("marca") String marca,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "wattsConsumo", required = false) Integer wattsConsumo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "arquitectura", required = false) String arquitectura,
            @RequestParam(value = "categoria", required = false) String categoriaNombre,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes) {

        try {
            service.create(
                    nombre,
                    marca,
                    precio,
                    stock,
                    wattsConsumo,
                    descripcion,
                    arquitectura,
                    categoriaNombre,
                    imagenes
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse("Producto creado correctamente", HttpStatus.CREATED.value()));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al crear producto: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping(
            value = "/{id}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse> updateProducto(
            @PathVariable int id,
            @RequestParam("nombre") String nombre,
            @RequestParam("marca") String marca,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "wattsConsumo", required = false) Integer wattsConsumo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "arquitectura", required = false) String arquitectura,
            @RequestParam(value = "categoria", required = false) String categoriaNombre,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes) {

        try {
            service.update(id, nombre, marca, precio, stock, wattsConsumo, descripcion,
                    arquitectura, categoriaNombre, imagenes);

            return ResponseEntity.ok(new ApiResponse("Producto modificado con éxito", HttpStatus.OK.value()));
        } catch (ErrorService e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar el producto: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProductos() {
        try {
            List<Producto> listEntity = service.listProductos();

            if (listEntity.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No hay productos registrados", HttpStatus.NO_CONTENT.value()));
            }

            return ResponseEntity.ok(listEntity);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al obtener los productos: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.read(id));
        } catch (ErrorService e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al buscar el producto: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProducto(@PathVariable int id) {
        try {
            service.delete(service.read(id));
            return ResponseEntity.ok(new ApiResponse("Producto eliminado con éxito", HttpStatus.OK.value()));
        } catch (ErrorService e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar el producto: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/imagenes/{filename:.+}")
    public ResponseEntity<?> verImagen(@PathVariable String filename) {
        try {
            Resource recurso = storageService.cargarArchivo(filename);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(recurso);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Imagen no encontrada: " + e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al recuperar la imagen: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}