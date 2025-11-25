# Etapa de compilación: utiliza Gradle con JDK 17 para construir la aplicación
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

# Convertir CRLF a LF y dar permisos de ejecución
RUN dos2unix ./gradle/wrapper/gradle-wrapper.sh 2>/dev/null || sed -i 's/\r$//' ./gradle/wrapper/gradle-wrapper.sh
RUN chmod +x ./gradle/wrapper/gradle-wrapper.sh

# Compilar sin tests
RUN gradle build -x test --no-daemon

# Etapa de ejecución: JRE ligero
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Puerto expuesto (la aplicación usará la variable de entorno PORT en Render)
EXPOSE 8080

# Copiar el JAR generado desde la etapa de build
COPY --from=build /app/build/libs/*.jar ./app.jar

# Comando de arranque
ENTRYPOINT ["java", "-jar", "./app.jar"]
