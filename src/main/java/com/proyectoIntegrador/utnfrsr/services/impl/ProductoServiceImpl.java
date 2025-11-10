package com.proyectoIntegrador.utnfrsr.services.impl;

import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Categoria;
import com.proyectoIntegrador.utnfrsr.models.Imagen;
import com.proyectoIntegrador.utnfrsr.models.Producto;
import com.proyectoIntegrador.utnfrsr.repository.CategoriaRepository;
import com.proyectoIntegrador.utnfrsr.repository.ProductoRepository;
import com.proyectoIntegrador.utnfrsr.services.ProductoService;
import com.proyectoIntegrador.utnfrsr.services.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final StorageService storageService;

    @Autowired
    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, StorageService storageService){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.storageService = storageService;
    }

    @Transactional
    @Override
    public void create(
            String nombre,
            String marca,
            Double precio,
            Integer stock,
            Integer wattsConsumo,
            String descripcion,
            String arquitectura,
            String categoriaNombre,
            List<MultipartFile> imagenes) {

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setMarca(marca);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setWattsConsumo(wattsConsumo);
        producto.setDescripcion(descripcion);
        producto.setArquitectura(arquitectura);

        if (categoriaNombre != null && !categoriaNombre.isBlank()) {
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase(categoriaNombre)
                    .orElseThrow(() -> new ErrorService("Categoría no encontrada: " + categoriaNombre));
            producto.setCategorias(new HashSet<>(List.of(categoria)));
        }

        if (imagenes != null && !imagenes.isEmpty()) {
            List<Imagen> nuevasImagenes = imagenes.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String nombreArchivo = storageService.guardarArchivo(file);
                        Imagen img = new Imagen();
                        img.setNombreArchivo(nombreArchivo);
                        img.setUrl("/uploads/productos/" + nombreArchivo);
                        img.setProducto(producto);
                        return img;
                    })
                    .toList();

            producto.setImagenesSinReferenciaCompartida(nuevasImagenes);
        }

        productoRepository.save(producto);
    }

    @Transactional
    @Override
    public void update(
            int id,
            String nombre,
            String marca,
            Double precio,
            Integer stock,
            Integer wattsConsumo,
            String descripcion,
            String arquitectura,
            String categoriaNombre,
            List<MultipartFile> imagenes) {

        Producto entity = read(id);

        entity.setNombre(nombre);
        entity.setMarca(marca);
        entity.setPrecio(precio);
        entity.setStock(stock);
        entity.setWattsConsumo(wattsConsumo);
        entity.setDescripcion(descripcion);
        entity.setArquitectura(arquitectura);

        if (categoriaNombre != null && !categoriaNombre.isBlank()) {
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase(categoriaNombre)
                    .orElseThrow(() -> new ErrorService("Categoría no encontrada: " + categoriaNombre));
            entity.setCategorias(new HashSet<>(List.of(categoria)));
        }

        if (imagenes != null && !imagenes.isEmpty()) {
            List<Imagen> nuevasImagenes = imagenes.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .map(file -> {
                        String nombreArchivo = storageService.guardarArchivo(file);
                        Imagen img = new Imagen();
                        img.setNombreArchivo(nombreArchivo);
                        img.setUrl("/uploads/productos/" + nombreArchivo);
                        img.setProducto(entity);
                        return img;
                    })
                    .toList();

            if (!nuevasImagenes.isEmpty()) {
                entity.setImagenesSinReferenciaCompartida(nuevasImagenes);
            }
        }

        productoRepository.save(entity);
    }

    @Override
    public void delete(Producto producto){
        Producto entity = read(producto.getId());
        productoRepository.delete(entity);
    }

    @Override
    public Producto read(int id){
        Optional<Producto> result = productoRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        } else {
            throw new ErrorService("Producto con id " + id + " no encontrado");
        }
    }

    @Override
    public List<Producto> listProductos(){
        return productoRepository.findAll();
    }
}