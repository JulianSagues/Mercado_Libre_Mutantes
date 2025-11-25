# Etapa de compilación: utiliza Gradle con JDK 17 para construir la aplicación
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# Descargar dependencias en cache
RUN ./gradlew dependencies --no-daemon

# Copiar el código fuente y compilar sin tests
COPY src ./src
RUN ./gradlew build -x test --no-daemon

# Etapa de ejecución: JRE ligero
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Puerto expuesto (la aplicación usará la variable de entorno PORT en Render)
EXPOSE 8080

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/build/libs/*.jar ./app.jar

# Comando de arranque
ENTRYPOINT ["java", "-jar", "./app.jar"]
