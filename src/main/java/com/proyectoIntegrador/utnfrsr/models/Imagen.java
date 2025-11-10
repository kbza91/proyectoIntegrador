package com.proyectoIntegrador.utnfrsr.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombreArchivo;
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonBackReference(value = "producto-imagen")
    private Producto producto;

    @OneToOne(mappedBy = "imagen")
    @JsonBackReference(value = "categoria-imagen")
    private Categoria categoria;

    @JsonProperty("urlCompleta")
    public String getUrlCompleta() {
        if (this.url == null) return null;

        String baseUrl = "http://localhost:8080";
        if (this.url.startsWith("/")) {
            return baseUrl + this.url;
        } else {
            return baseUrl + "/" + this.url;
        }
    }
}
