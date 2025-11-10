package com.proyectoIntegrador.utnfrsr.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.proyectoIntegrador.utnfrsr.auth.model.User;
import com.proyectoIntegrador.utnfrsr.auth.repository.UserRepository;
import com.proyectoIntegrador.utnfrsr.enums.EstadoPedido;
import com.proyectoIntegrador.utnfrsr.error.ErrorService;
import com.proyectoIntegrador.utnfrsr.models.Pedido;
import com.proyectoIntegrador.utnfrsr.models.Producto;
import com.proyectoIntegrador.utnfrsr.repository.PedidoRepository;
import com.proyectoIntegrador.utnfrsr.repository.ProductoRepository;
import com.proyectoIntegrador.utnfrsr.services.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mercadopago.client.preference.PreferenceItemRequest;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;

    @Value("${utnfrsr.accesstoken}")
    private String accessToken;

    @Autowired
    public PedidoServiceImpl(PedidoRepository pedidoRepository, UserRepository userRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void create(Pedido pedido) {
        pedidoRepository.save(pedido);
    }

    @Override
    public void update(int id, Pedido pedido) {
        Pedido entity = read(id);

        entity.setUsuario(pedido.getUsuario());
        entity.setProductos(pedido.getProductos());
        entity.setEstado(pedido.getEstado());
        // El montoTotal se recalcula automáticamente con @PreUpdate

        pedidoRepository.save(entity);
    }

    @Override
    public void delete(int id) {
        Pedido entity = read(id);
        pedidoRepository.delete(entity);
    }

    @Override
    public Pedido read(int id) {
        Optional<Pedido> result = pedidoRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        throw new ErrorService("Pedido con id " + id + " no encontrado");
    }

    @Override
    public List<Pedido> listPedidos() {
        return pedidoRepository.findAll();
    }

    @Override
    public List<Pedido> listPedidosByUsuario(int usuarioId) {
        return pedidoRepository.findByUsuario_Id(usuarioId);
    }

    @Override
    public Preference crearPedidoYGenerarPago(HttpServletRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonBody = mapper.readTree(request.getInputStream());

            Pedido entitySave = new Pedido();

            Long usuarioId = jsonBody.has("usuarioId") ? jsonBody.get("usuarioId").asLong() : null;
            if (usuarioId == null) {
                throw new ErrorService("El pedido debe tener un usuario asociado.");
            }

            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new ErrorService("Usuario no encontrado con ID: " + usuarioId));
            entitySave.setUsuario(usuario);

            List<Producto> productos = new ArrayList<>();
            if (jsonBody.has("productos") && jsonBody.get("productos").isArray()) {
                for (JsonNode prod : jsonBody.get("productos")) {
                    Integer idProducto = prod.get("id").asInt();
                    Producto producto = productoRepository.findById(idProducto)
                            .orElseThrow(() -> new ErrorService("Producto no encontrado con ID: " + idProducto));
                    productos.add(producto);
                }
            } else {
                throw new ErrorService("El pedido no tiene productos asociados.");
            }
            entitySave.setProductos(productos);

            // ====== MONTO ======
            Double montoTotal = jsonBody.has("montoTotal") ? jsonBody.get("montoTotal").asDouble() : null;
            if (montoTotal == null || montoTotal <= 0) {
                throw new ErrorService("El monto total del pedido no puede ser 0.");
            }
            entitySave.setMontoTotal(montoTotal);

            entitySave.setEstado(EstadoPedido.PENDIENTE);

            Pedido saved = pedidoRepository.save(entitySave);
            System.out.println("🟢 Pedido guardado correctamente con " +
                    saved.getProductos().size() + " productos y montoTotal = " + saved.getMontoTotal());

            return generarPagoParaPedido(saved);

        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al crear pedido o generar pago: " + e.getMessage());
        }
}

    @Override
    public Preference generarPagoDesdePedidoExistente(Integer pedidoId) {
        try {
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new ErrorService("Pedido no encontrado con ID: " + pedidoId));

            if (pedido.getMontoTotal() <= 0) {
                throw new ErrorService("El monto total del pedido no puede ser 0.");
            }

            if (pedido.getEstado() == EstadoPedido.PAGADO) {
                throw new ErrorService("El pedido ya fue completado.");
            }

            pedido.setEstado(EstadoPedido.PENDIENTE);
            pedidoRepository.save(pedido);

            return generarPagoParaPedido(pedido);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al generar pago desde pedido existente: " + e.getMessage());
        }
    }

    private Preference generarPagoParaPedido(Pedido pedido) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5500/success.html")
                    .pending("http://localhost:5500/pending.html")
                    .failure("http://localhost:5500/failure.html")
                    .build();

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(pedido.getId().toString())
                    .title("Pedido #" + pedido.getId())
                    .description("Compra en Tienda 11 Ratas 1 Sueño")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(BigDecimal.valueOf(pedido.getMontoTotal()))
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(List.of(itemRequest))
                    .backUrls(backUrls)
                    .build();

            PreferenceClient client = new PreferenceClient();
            return client.create(preferenceRequest);

        } catch (MPApiException | MPException e) {
            System.err.println("❌ Error Mercado Pago: " + e.getMessage());
            if (e instanceof MPApiException apiEx) {
                System.err.println("Status: " + apiEx.getApiResponse().getStatusCode());
                System.err.println("Body: " + apiEx.getApiResponse().getContent());
            }
            throw new ErrorService("Error al procesar el pago con Mercado Pago: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error general al generar pago: " + e.getMessage());
        }
    }
}
