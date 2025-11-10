package com.proyectoIntegrador.utnfrsr.controllers;

import com.mercadopago.resources.preference.Preference;
import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Pedido;
import com.proyectoIntegrador.utnfrsr.services.PedidoService;
import com.proyectoIntegrador.utnfrsr.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody Pedido pedido) {
        try {
            service.create(pedido);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse("Pedido creado correctamente", HttpStatus.CREATED.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al crear el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPedidos() {
        try {
            List<Pedido> listEntity = service.listPedidos();
            if (listEntity.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No hay pedidos registrados", HttpStatus.NO_CONTENT.value()));
            }
            return ResponseEntity.ok(listEntity);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al obtener los pedidos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> getPedidosByUsuario(@PathVariable int usuarioId) {
        try {
            List<Pedido> listEntity = service.listPedidosByUsuario(usuarioId);
            if (listEntity.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse("No hay pedidos para este usuario", HttpStatus.NO_CONTENT.value()));
            }
            return ResponseEntity.ok(listEntity);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al obtener los pedidos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable int id, @Valid @RequestBody Pedido pedido) {
        try {
            service.update(id, pedido);
            return ResponseEntity.ok(new ApiResponse("Pedido modificado con éxito", HttpStatus.OK.value()));
        } catch (ErrorService e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al actualizar el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPedido(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.read(id));
        } catch (ErrorService e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al buscar el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePedido(@PathVariable int id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new ApiResponse("Pedido eliminado con éxito", HttpStatus.OK.value()));
        } catch (ErrorService e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error al eliminar el pedido: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/pago")
    public ResponseEntity<?> crearPedidoYGenerarPago(HttpServletRequest request) {
        try {
            Preference preference = service.crearPedidoYGenerarPago(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(preference);
        } catch (ErrorService e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno al generar pago: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/pago/{pedidoId}")
    public ResponseEntity<?> generarPagoDesdePedido(@PathVariable Integer pedidoId) {
        try {
            Preference preference = service.generarPagoDesdePedidoExistente(pedidoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(preference);
        } catch (ErrorService e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno al generar pago: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}