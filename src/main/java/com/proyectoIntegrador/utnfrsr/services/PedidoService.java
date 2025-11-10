package com.proyectoIntegrador.utnfrsr.services;

import com.mercadopago.resources.preference.Preference;
import com.proyectoIntegrador.utnfrsr.models.Pedido;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface PedidoService {

    void create(Pedido pedido);

    void update(int id, Pedido pedido);

    void delete(int id);

    Pedido read(int id);

    List<Pedido> listPedidos();

    List<Pedido> listPedidosByUsuario(int usuarioId);

    Preference crearPedidoYGenerarPago(HttpServletRequest request);

    Preference generarPagoDesdePedidoExistente(Integer pedidoId);
}