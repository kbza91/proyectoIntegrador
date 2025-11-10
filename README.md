
# Back End - Documentación

Este proyecto implementa todo lo relacionado al Back End, usando Java 21 como lenguaje principal con SpringBoot como framework y PostgreSQL como base de datos.

---

## 📂 Estructura principal

```
Proyecto Integrador 4 Semestre/
│
├── Proyecto E-commerce Back [utnfrsr]/
│   ├── src/
│       ├── main/          
│           ├── java/           
│             ├── com.proyectoIntegrador.utnfrsr/
|                 ├── auth/                                        # Donde manejamos lo relacionado a autenticación y seguridad
|                     ├── config/                                    # Manejo de Seguridad
|                         └── SecurityConfiguration.java
│                     ├── controller/                                # Manejo de endpoints
│                         └── UserAuthController.java
|                     ├── enums/                                     # Manejo de Roles en un enum
│                         └── Rol.java
|                     ├── exceptions/                                # Manejo de Excepciones realionado al Usuario
│                         └── UserException.java
|                     ├── model/                                     # Definición de modelos
│                         └── User.java
|                     ├── repository/                                # Guardado en la base de datos
|                         └── UserRepository.java                    
|                     └── service/                                   # Manejo de lógica de los datos recibidos
|                         ├── impl/
|                             ├── JwtUtils.java                      # Manejo de lógica del token
|                             └── UserDetailsCustomService.java      # Configuración de los usuarios
|                         └── UserService.java
|
|                                                                  # Todo lo demás no relacionado a seguridad y autenticación
|                                                                    
|                 ├── controllers/                                   # Manejo de endpoints
|                     └── CategoriaController.java
|                     └── ProductoController.java
|                 ├── models/                                        # Definición de modelos
|                     └── Producto.java
|                     └── Categoria.java
|                 ├── repository/                                    # Guardado en la base de datos
|                     └── ProductoRepository.java
|                     └── CategoriaRepository.java
|                 └── services/                                      # Manejo de lógica de los datos recibidos
|                     ├── impl/
|                         └── ProductoServiceImpl
|                         └── CategoriaServiceImpl
|                     └── ProductoService
|                     └── CategoriaService
|             └── UtnfrsrApplication.java                            # Punto de arranque de la app
|          └── resources/                                            # Configuración del proyecto
|              └── application.properties          
|    └── .gitignore                                                # Configuración de que no dejar que se suba a GitHub
│    └── pom.xml                                                   # Dependencias, información y versión del proyecto
└──  └── README.md                                                 # Información del proyecto
