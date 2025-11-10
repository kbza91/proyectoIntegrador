package com.proyectoIntegrador.utnfrsr.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="producto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La marca no puede estar vacío")
    private String marca;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Double precio;

    private Integer wattsConsumo;

    @Column(length = 2000)
    private String descripcion;

    @NotNull(message = "El stock es obligatorio")
    private Integer stock;

    private String arquitectura;

    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "producto-imagen")
    private List<Imagen> imagenes = new ArrayList<>();

    @ManyToMany(mappedBy = "productos")
    private List<Pedido> pedidos = new ArrayList<>();

    public void setImagenesSinReferenciaCompartida(List<Imagen> nuevasImagenes) {
        this.imagenes.clear();
        if (nuevasImagenes != null) {
            for (Imagen img : nuevasImagenes) {
                if (img != null && (img.getNombreArchivo() != null || img.getUrl() != null)) {
                    img.setProducto(this);
                    this.imagenes.add(img);
                }
            }
        }
    }
}
