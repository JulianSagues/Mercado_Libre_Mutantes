# Etapa de compilación
FROM alpine:latest as build

RUN apk update
RUN apk add openjdk17

# Copiar código fuente
COPY . .

# Compilar con Maven
RUN chmod +x ./mvnw
RUN ./mvnw clean install -DskipTests

# Etapa de ejecución
FROM openjdk:17-alpine

EXPOSE 8080

# Copiar JAR compilado
COPY --from=build ./target/MercadoLibre-1.0-SNAPSHOT.jar ./app.jar

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]