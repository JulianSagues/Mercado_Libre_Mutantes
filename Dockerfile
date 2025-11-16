# Etapa de compilación: utiliza Maven con JDK 17 para construir la aplicación
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar pom e instalar dependencias en cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar sin tests
COPY src ./src
RUN mvn clean package -DskipTests -DskipITs

# Etapa de ejecución: JRE ligero
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Puerto expuesto (la aplicación usará la variable de entorno PORT en Render)
EXPOSE 8080

# Copiar el JAR generado desde la etapa de build (acepta nombre variable)
COPY --from=build /app/target/*.jar ./app.jar

# Comando de arranque
ENTRYPOINT ["java", "-jar", "./app.jar"]
