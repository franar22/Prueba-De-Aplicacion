# =============================================
# ETAPA 1: BUILD
# Compilamos el JAR con Maven dentro del contenedor.
# Así no necesitas tener Maven instalado en el host.
# =============================================
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar la caché de Docker.
# Si el pom no cambia, Docker reutiliza la capa de dependencias.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -B

# =============================================
# ETAPA 2: RUNTIME
# Imagen final ligera, solo lleva el JAR.
# =============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Usuario sin privilegios root (buena práctica de seguridad)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiamos el JAR generado en la etapa anterior
COPY --from=builder /app/target/tasks-api-*.jar app.jar

# Puerto que expone el microservicio (igual que server.port)
EXPOSE 3000

# Healthcheck: Docker verifica que el servicio esté vivo
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:3000/api/tasks/health || exit 1

# Variables de entorno por defecto (sobreescribibles al hacer docker run)
ENV DB_HOST=db \
    DB_PORT=3306 \
    DB_NAME=tasksdb \
    DB_USER=taskuser \
    DB_PASSWORD=taskpass \
    ALLOWED_ORIGINS=*

# Arranca la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
