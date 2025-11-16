# Etapa de compilación
FROM eclipse-temurin:17-alpine as build

WORKDIR /app

# Copiar código fuente
COPY . .

# Compilar con Maven
RUN chmod +x ./mvnw
RUN ./mvnw clean install -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine

EXPOSE 8080

# Copiar JAR compilado
COPY --from=build ./target/MercadoLibre-1.0-SNAPSHOT.jar ./app.jar

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]