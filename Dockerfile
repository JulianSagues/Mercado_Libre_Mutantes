# Etapa de compilación
FROM maven:3.9-eclipse-temurin-17-alpine as build

WORKDIR /app

# Copiar pom.xml primero (para cache de dependencias)
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar con Maven
RUN mvn clean install -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

EXPOSE 8080

# Copiar JAR compilado
COPY --from=build /app/target/MercadoLibre-1.0-SNAPSHOT.jar ./app.jar

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]