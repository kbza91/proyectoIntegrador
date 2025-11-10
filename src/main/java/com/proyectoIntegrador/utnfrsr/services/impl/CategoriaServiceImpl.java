package com.proyectoIntegrador.utnfrsr.services.impl;

import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Categoria;
import com.proyectoIntegrador.utnfrsr.repository.CategoriaRepository;
import com.proyectoIntegrador.utnfrsr.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void create(Categoria categoria){
        categoriaRepository.save(categoria);
    }

    @Override
    public void update(int id, Categoria categoria) {
        Categoria entity = read(id);

        entity.setNombre(categoria.getNombre());

        if (categoria.getImagen() != null) {
            entity.setImagen(categoria.getImagen());
        }

        categoriaRepository.save(entity);
    }

    @Override
    public void delete(Categoria categoria){
        Categoria entity = read(categoria.getId());
        categoriaRepository.delete(entity);
    }

    @Override
    public Categoria read(int id){
        Optional<Categoria> result = categoriaRepository.findById(id);
        if (result.isPresent()){
            return result.get();
        }else{
            throw new ErrorService("Categoría no encontrada");
        }
    }

    @Override
    public List<Categoria> listCategoria(){
        return categoriaRepository.findAll();
    }
}
