package com.proyectoIntegrador.utnfrsr.controllers;

import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Categoria;
import com.proyectoIntegrador.utnfrsr.models.Imagen;
import com.proyectoIntegrador.utnfrsr.services.CategoriaService;
import com.proyectoIntegrador.utnfrsr.services.StorageService;
import com.proyectoIntegrador.utnfrsr.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService service;
    private final StorageService storageService;

    public CategoriaController(CategoriaService service, StorageService storageService){
        this.service = service;
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> create(
            @RequestPart("nombre") String nombre,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {

        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);

            // Si hay una imagen, la guardamos y la asociamos
            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = storageService.guardarArchivoCategoria(imagen);

                Imagen nuevaImagen = new Imagen();
                nuevaImagen.setNombreArchivo(nombreArchivo);
                nuevaImagen.setUrl("/uploads/categorias/" + nombreArchivo);
                nuevaImagen.setCategoria(categoria);

                categoria.setImagen(nuevaImagen);
            }

            service.create(categoria);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse("Categoría creada correctamente", HttpStatus.CREATED.value()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al crear la categoría: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCategorias(){
        try {
            List<Categoria> listEntity = service.listCategoria();

            if (listEntity.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No hay categorías registradas", HttpStatus.NO_CONTENT.value()));
            }

            return ResponseEntity.ok(listEntity);
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al obtener las categorías: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> update(
            @PathVariable int id,
            @RequestPart("nombre") String nombre,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) {
        try {
            // Buscar categoría existente
            Categoria categoria = service.read(id);
            categoria.setNombre(nombre);

            // Si se subió una nueva imagen, la guardamos y actualizamos la relación
            if (imagen != null && !imagen.isEmpty()) {
                String nombreArchivo = storageService.guardarArchivoCategoria(imagen);

                Imagen imagenExistente = categoria.getImagen();
                if (imagenExistente != null) {
                    imagenExistente.setNombreArchivo(nombreArchivo);
                    imagenExistente.setUrl("/uploads/categorias/" + nombreArchivo);
                } else {
                    Imagen nuevaImagen = new Imagen();
                    nuevaImagen.setNombreArchivo(nombreArchivo);
                    nuevaImagen.setUrl("/uploads/categorias/" + nombreArchivo);
                    nuevaImagen.setCategoria(categoria);
                    categoria.setImagen(nuevaImagen);
                }
            }

            service.update(id, categoria);

            return ResponseEntity
                    .ok(new ApiResponse("Categoría modificada con éxito", HttpStatus.OK.value()));

        } catch (ErrorService e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar la categoría: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoria(@PathVariable int id){
        try{
            return ResponseEntity.ok(service.read(id));
        }catch(ErrorService e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al buscar la categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategoria(@PathVariable int id){
        try{
            service.delete(service.read(id));
            return ResponseEntity.ok(new ApiResponse("Categoría eliminada con éxito", HttpStatus.OK.value()));
        }catch(ErrorService e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar la categoría: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/{id}/imagen")
    public ResponseEntity<ApiResponse> subirImagen(@PathVariable int id, @RequestParam("file") MultipartFile file) {
        try {
            Categoria categoria = service.read(id);

            if (categoria == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Categoría no encontrada", HttpStatus.NOT_FOUND.value()));
            }

            String nombreArchivo = storageService.guardarArchivo(file);
            Imagen imagen = new Imagen();
            imagen.setNombreArchivo(nombreArchivo);
            imagen.setUrl("/api/categorias/imagen/" + nombreArchivo);
            imagen.setCategoria(categoria);

            categoria.setImagen(imagen);
            service.update(id, categoria);

            return ResponseEntity
                    .ok(new ApiResponse("Imagen subida correctamente", HttpStatus.OK.value()));

        } catch (ErrorService e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al subir la imagen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
