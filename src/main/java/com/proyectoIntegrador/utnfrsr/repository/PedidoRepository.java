package com.proyectoIntegrador.utnfrsr.repository;

import com.proyectoIntegrador.utnfrsr.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario_Id(Integer usuarioId);

}