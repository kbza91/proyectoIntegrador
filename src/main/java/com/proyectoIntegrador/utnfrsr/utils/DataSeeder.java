package com.proyectoIntegrador.utnfrsr.utils;

import com.proyectoIntegrador.utnfrsr.auth.enums.Rol;
import com.proyectoIntegrador.utnfrsr.auth.model.User;
import com.proyectoIntegrador.utnfrsr.auth.repository.UserRepository;
import com.proyectoIntegrador.utnfrsr.models.Categoria;
import com.proyectoIntegrador.utnfrsr.models.Imagen;
import com.proyectoIntegrador.utnfrsr.models.Producto;
import com.proyectoIntegrador.utnfrsr.repository.CategoriaRepository;
import com.proyectoIntegrador.utnfrsr.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoriaRepository categoriaRepository, ProductoRepository productoRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0 && productoRepository.count() == 0) {
            System.out.println("🧩 Insertando datos iniciales...");

            // Crear categorías
            Categoria catCPU = new Categoria();
            catCPU.setNombre("Procesadores");

            Imagen imgCat1 = new Imagen();
            imgCat1.setNombreArchivo("procesadores.jpeg");
            imgCat1.setUrl("/img/seed/procesadores.jpeg");
            imgCat1.setCategoria(catCPU);
            catCPU.setImagen(imgCat1);

            Categoria catGPU = new Categoria();
            catGPU.setNombre("Placas de Video");

            Imagen imgCat2 = new Imagen();
            imgCat2.setNombreArchivo("placas_video.jpeg");
            imgCat2.setUrl("/img/seed/placas_video.jpeg");
            imgCat2.setCategoria(catGPU);
            catGPU.setImagen(imgCat2);

            Categoria catRAM = new Categoria();
            catRAM.setNombre("Memorias RAM");

            Imagen imgCat3 = new Imagen();
            imgCat3.setNombreArchivo("rams.jpeg");
            imgCat3.setUrl("/img/seed/rams.jpeg");
            imgCat3.setCategoria(catRAM);
            catRAM.setImagen(imgCat3);

            Categoria catMother = new Categoria();
            catMother.setNombre("Motherboards");

            Imagen imgCat4 = new Imagen();
            imgCat4.setNombreArchivo("motherboards.jpeg");
            imgCat4.setUrl("/img/seed/motherboards.jpeg");
            imgCat4.setCategoria(catMother);
            catMother.setImagen(imgCat4);

            Categoria catPerifericos = new Categoria();
            catPerifericos.setNombre("Perifericos");

            Imagen imgCat5 = new Imagen();
            imgCat5.setNombreArchivo("perifericos.jpeg");
            imgCat5.setUrl("/img/seed/perifericos.jpeg");
            imgCat5.setCategoria(catPerifericos);
            catPerifericos.setImagen(imgCat5);

            Categoria catPCAO = new Categoria();
            catPCAO.setNombre("PC de Oficina");

            Imagen imgCatPCAO = new Imagen();
            imgCatPCAO.setNombreArchivo("office_pc.webp");
            imgCatPCAO.setUrl("/img/seed/office_pc.webp");
            imgCatPCAO.setCategoria(catPCAO);
            catPCAO.setImagen(imgCatPCAO);

            Categoria catPCAB = new Categoria();
            catPCAB.setNombre("PC Gamer Básica");

            Imagen imgCatPCAB = new Imagen();
            imgCatPCAB.setNombreArchivo("basic_pc.jpeg");
            imgCatPCAB.setUrl("/img/seed/basic_pc.jpeg");
            imgCatPCAB.setCategoria(catPCAB);
            catPCAB.setImagen(imgCatPCAB);

            Categoria catPcAA = new Categoria();
            catPcAA.setNombre("PC Gamer Avanzada");

            Imagen imgCatPCAA = new Imagen();
            imgCatPCAA.setNombreArchivo("advanced_pc.webp");
            imgCatPCAA.setUrl("/img/seed/advanced_pc.webp");
            imgCatPCAA.setCategoria(catPcAA);
            catPcAA.setImagen(imgCatPCAA);

            categoriaRepository.saveAll(List.of(catCPU, catGPU, catRAM, catMother, catPerifericos, catPCAO, catPCAB, catPcAA));

            // Crear productos
            Producto p1 = new Producto();
            p1.setNombre("Intel Core i5 12400F");
            p1.setMarca("Intel");
            p1.setPrecio(120000.0);
            p1.setWattsConsumo(65);
            p1.setDescripcion("Procesador de 6 núcleos y 12 hilos, ideal para gaming.");
            p1.setStock(15);
            p1.setArquitectura("Alder Lake");
            p1.setCategorias(Set.of(catCPU));

            Imagen img1 = new Imagen();
            img1.setNombreArchivo("intel_core_i5.jpeg");
            img1.setUrl("/img/seed/intel_core_i5.jpeg");
            img1.setProducto(p1);
            p1.setImagenes(List.of(img1));

            Producto p2 = new Producto();
            p2.setNombre("NVIDIA RTX 3060");
            p2.setMarca("NVIDIA");
            p2.setPrecio(380000.0);
            p2.setDescripcion("Placa de video con 12GB GDDR6, ideal para gaming y trabajo gráfico.");
            p2.setStock(8);
            p2.setCategorias(Set.of(catGPU));

            Imagen img2 = new Imagen();
            img2.setNombreArchivo("nvidia_rtx_3060.jpeg");
            img2.setUrl("/img/seed/nvidia_rtx_3060.jpeg");
            img2.setProducto(p2);
            p2.setImagenes(List.of(img2));

            Producto p3 = new Producto();
            p3.setNombre("Kingston Fury 16GB DDR4");
            p3.setMarca("Kingston");
            p3.setPrecio(45000.0);
            p3.setDescripcion("Memoria RAM DDR4 de 3200MHz, ideal para alto rendimiento.");
            p3.setStock(20);
            p3.setCategorias(Set.of(catRAM));

            Imagen img3 = new Imagen();
            img3.setNombreArchivo("kingston_fury_16gb_ddr4.jpeg");
            img3.setUrl("/img/seed/kingston_fury_16gb_ddr4.jpeg");
            img3.setProducto(p3);
            p3.setImagenes(List.of(img3));

            Producto p4 = new Producto();
            p4.setNombre("ASUS TUF Gaming B660M-PLUS");
            p4.setMarca("ASUS");
            p4.setPrecio(160000.0);
            p4.setDescripcion("Motherboard para procesadores Intel de 12va generación.");
            p4.setStock(10);
            p4.setCategorias(Set.of(catMother));

            Imagen img4 = new Imagen();
            img4.setNombreArchivo("asus_tuf_gaming_b660m_plus.jpeg");
            img4.setUrl("/img/seed/asus_tuf_gaming_b660m_plus.jpeg");
            img4.setProducto(p4);
            p4.setImagenes(List.of(img4));

            Producto p5 = new Producto();
            p5.setNombre("Logitech K380s Bluetooth");
            p5.setMarca("Logitech");
            p5.setPrecio(53000.0);
            p5.setDescripcion("Teclado inalámbrico compacto con conexión Bluetooth. Compatible con Windows, macOS, Android y más.");
            p5.setStock(15);
            p5.setCategorias(Set.of(catPerifericos));

            Imagen img5 = new Imagen();
            img5.setNombreArchivo("logitech_k380s_bluetooth.jpeg");
            img5.setUrl("/img/seed/logitech_k380s_bluetooth.jpeg");
            img5.setProducto(p5);
            p5.setImagenes(List.of(img5));

            // Crear productos Armados - Office
            Producto pAO = new Producto();
            pAO.setNombre("PC de Oficina optimizada para trabajo. Incluye AMD Ryzen 5 3400G - 16GB RAM - 512GB SSD");
            pAO.setMarca("AMD");
            pAO.setPrecio(371498.0);
            pAO.setDescripcion("Gabinete KIT Spartan A1 + Fuente + Teclado y Mouse de Regalo - " +
                    "Disco Solido SSD ADATA 512GB SU650SS 520MB/s - " +
                    "Memoria Team DDR4 8GB 3200MHz T-Force Vulcan Z Grey CL16 - " +
                    "Mother Asrock A520M-HDV AM4 - " +
                    "Procesador AMD RYZEN 5 3400G 4.2GHz Turbo + Radeon Vega 11 AM4 Wraith Spire Cooler.");
            pAO.setStock(10);
            pAO.setCategorias(Set.of(catPCAO));

            Imagen imgAO1 = new Imagen();
            imgAO1.setNombreArchivo("office_gabinete.jpg");
            imgAO1.setUrl("/img/seed/office_gabinete.jpg");
            imgAO1.setProducto(pAO);

            Imagen imgAO2 = new Imagen();
            imgAO2.setNombreArchivo("office_ram.jpg");
            imgAO2.setUrl("/img/seed/office_ram.jpg");
            imgAO2.setProducto(pAO);

            Imagen imgAO3 = new Imagen();
            imgAO3.setNombreArchivo("office_cpu.jpg");
            imgAO3.setUrl("/img/seed/office_cpu.jpg");
            imgAO3.setProducto(pAO);

            Imagen imgAO4 = new Imagen();
            imgAO4.setNombreArchivo("office_ssd.jpg");
            imgAO4.setUrl("/img/seed/office_ssd.jpg");
            imgAO4.setProducto(pAO);

            Imagen imgAO5 = new Imagen();
            imgAO5.setNombreArchivo("office_mother.jpg");
            imgAO5.setUrl("/img/seed/office_mother.jpg");
            imgAO5.setProducto(pAO);
            pAO.setImagenes(List.of(imgAO1, imgAO2, imgAO3, imgAO4, imgAO5));

            // Crear productos Armados - Basic
            Producto pAB = new Producto();
            pAB.setNombre("PC Gamer Básica ideal para juegos ligeros. Incluye AMD Ryzen 7 5700G - 16GB RAM - 512GB SSD");
            pAB.setMarca("AMD");
            pAB.setPrecio(446962.0);
            pAB.setDescripcion("Gabinete Checkpoint SHADOW RGB 4x120mm RGB Fixed Fans Vidrio Templado ATX - " +
                    "Fuente Cougar 600W 80 Plus Bronze ATLAS - " +
                    "Memoria Team DDR4 8GB 3200MHz T-Force Vulcan Z Grey CL16 - " +
                    "Mother MSI A520M-A PRO DDR4 AM4 - " +
                    "Disco Solido SSD Team 512GB GX2 530MB/s - " +
                    "Procesador AMD RYZEN 5 3400G 4.2GHz Turbo + Radeon Vega 11 AM4 Wraith Spire Cooler." +
                    "");
            pAB.setStock(10);
            pAB.setCategorias(Set.of(catPCAB));

            Imagen imgAB1 = new Imagen();
            imgAB1.setNombreArchivo("basic_gabinete.jpg");
            imgAB1.setUrl("/img/seed/basic_gabinete.jpg");
            imgAB1.setProducto(pAB);

            Imagen imgAB2 = new Imagen();
            imgAB2.setNombreArchivo("basic_mother.jpg");
            imgAB2.setUrl("/img/seed/basic_mother.jpg");
            imgAB2.setProducto(pAB);

            Imagen imgAB3 = new Imagen();
            imgAB3.setNombreArchivo("basic_ram.jpg");
            imgAB3.setUrl("/img/seed/basic_ram.jpg");
            imgAB3.setProducto(pAB);

            Imagen imgAB4 = new Imagen();
            imgAB4.setNombreArchivo("basic_ssd.jpg");
            imgAB4.setUrl("/img/seed/basic_ssd.jpg");
            imgAB4.setProducto(pAB);

            Imagen imgAB5 = new Imagen();
            imgAB5.setNombreArchivo("basic_cpu.jpg");
            imgAB5.setUrl("/img/seed/basic_cpu.jpg");
            imgAB5.setProducto(pAB);

            Imagen imgAB6 = new Imagen();
            imgAB6.setNombreArchivo("basic_fuente.jpg");
            imgAB6.setUrl("/img/seed/basic_fuente.jpg");
            imgAB6.setProducto(pAB);
            pAB.setImagenes(List.of(imgAB1, imgAB2, imgAB3, imgAB4, imgAB5, imgAB6));

            // Crear productos Armados - Avanzada
            Producto pAA = new Producto();
            pAA.setNombre("PC Gamer Avanzada ideal para gaming de alto rendimiento. Incluye Mini ITX AMD Ryzen 5 5600XT 32GB DDR4 RTX 5060 1TB NVMe");
            pAA.setMarca("AMD");
            pAA.setPrecio(1763954.0);
            pAA.setDescripcion("Fuente Antec 850W 80 Plus Gold Full Modular SFX 4.1 ATX 3.1 PCIe 5.1 SF850X AR ITX - " +
                    "Disco Sólido SSD M.2 Kingston 1TB NV3 6000MB/s NVMe 2230 PCI-E Gen4 x4 - " +
                    "Memoria G.Skill DDR4 32GB (2x16GB) 3200MHz TRIDENT Z Silver/White CL16 XMP 2.0 - " +
                    "Cooler CPU XYZ Thermax 6 White ARGB - " +
                    "Gabinete Corsair 2000D RGB AIRFLOW White Mini ITX 3x120mm AF120 ARGB Slim Fans PSU SFX/L USB-C -" +
                    "Placa de video Zotac GeForce RTX 5060 8GB GDDR7 Twin Edge OC -" +
                    "Procesador AMD Ryzen 5 5600XT 4.6GHz Turbo + Wraith Stealth Cooler -" +
                    "Mother Asrock A520M-ITX/AC WIFI AM4 DDR4.");
            pAA.setStock(10);
            pAA.setCategorias(Set.of(catPcAA));

            Imagen imgAA1 = new Imagen();
            imgAA1.setNombreArchivo("advanced_fuente.jpg");
            imgAA1.setUrl("/img/seed/advanced_fuente.jpg");
            imgAA1.setProducto(pAA);

            Imagen imgAA2 = new Imagen();
            imgAA2.setNombreArchivo("advanced_m.2.jpg");
            imgAA2.setUrl("/img/seed/advanced_m.2.jpg");
            imgAA2.setProducto(pAA);

            Imagen imgAA3 = new Imagen();
            imgAA3.setNombreArchivo("advanced_ram.jpg");
            imgAA3.setUrl("/img/seed/advanced_ram.jpg");
            imgAA3.setProducto(pAA);

            Imagen imgAA4 = new Imagen();
            imgAA4.setNombreArchivo("advanced_cooler.jpg");
            imgAA4.setUrl("/img/seed/advanced_cooler.jpg");
            imgAA4.setProducto(pAA);

            Imagen imgAA5 = new Imagen();
            imgAA5.setNombreArchivo("advanced_gabinete.jpg");
            imgAA5.setUrl("/img/seed/advanced_gabinete.jpg");
            imgAA5.setProducto(pAA);

            Imagen imgAA6 = new Imagen();
            imgAA6.setNombreArchivo("advanced_gpu.jpg");
            imgAA6.setUrl("/img/seed/advanced_gpu.jpg");
            imgAA6.setProducto(pAA);

            Imagen imgAA7 = new Imagen();
            imgAA7.setNombreArchivo("advanced_cpu.jpg");
            imgAA7.setUrl("/img/seed/advanced_cpu.jpg");
            imgAA7.setProducto(pAA);

            Imagen imgAA8 = new Imagen();
            imgAA8.setNombreArchivo("advanced_mother.jpg");
            imgAA8.setUrl("/img/seed/advanced_mother.jpg");
            imgAA8.setProducto(pAA);
            pAA.setImagenes(List.of(imgAA5, imgAA2, imgAA3, imgAA4, imgAA1, imgAA6, imgAA7, imgAA8));

            productoRepository.saveAll(List.of(p1, p2, p3, p4, p5, pAO, pAB, pAA));

            // Crea un usuario administrador por defecto
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setNombre("Admin");
                admin.setApellido("Principal");
                admin.setUsername("admin01");
                admin.setEmail("admin01@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin2025"));
                admin.setRol(Rol.ADMIN);
                admin.setDireccion("Oficina Central");
                admin.setTelefono("123456789");

                userRepository.save(admin);
                System.out.println("✅ Usuario administrador creado por defecto.");
            }

            System.out.println("✅ Datos iniciales insertados correctamente.");
        } else {
            System.out.println("ℹ️ Ya existen datos en la base, no se insertaron productos por defecto.");
        }
    }
}