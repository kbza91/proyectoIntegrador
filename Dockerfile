# ============================
# Etapa 1 — Build de la app
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos los archivos necesarios para compilar
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto (sin ejecutar tests)
RUN mvn clean package -DskipTests


# ============================
# Etapa 2 — Imagen liviana de runtime
# ============================
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiamos el .jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el contenedor (Render usa la variable PORT)
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]