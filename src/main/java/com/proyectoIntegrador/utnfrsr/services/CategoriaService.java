package com.proyectoIntegrador.utnfrsr.services;

import com.proyectoIntegrador.utnfrsr.models.Categoria;

import java.util.List;

public interface CategoriaService {
    void create(Categoria categoria);

    void update(int id, Categoria categoria);

    void delete(Categoria categoria);

    Categoria read(int id);

    List<Categoria> listCategoria();
}