# 🧬 Mutant Detection API

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Gradle](https://img.shields.io/badge/Gradle-8.5-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

El presente readme ha sido realizado por inteligencia artificial.

## 📋 Descripción
API REST desarrollada en Java con Spring Boot que detecta si una secuencia de ADN pertenece a un mutante. El análisis busca **más de una secuencia de cuatro letras iguales** (A, T, C, G) en las siguientes direcciones:

- ➡️ **Horizontal**
- ⬇️ **Vertical**
- ↘️ **Diagonal descendente**
- ↗️ **Diagonal ascendente**

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.2.0 | Framework web |
| Gradle | 8.5 | Sistema de construcción |
| Spring Data JPA | 3.2.0 | Persistencia de datos |
| H2 Database | 2.2.224 | Base de datos en memoria |
| Swagger/OpenAPI | 2.2.0 | Documentación de API |
| JUnit 5 | 5.10.1 | Testing |
| Jacoco | 0.8.10 | Cobertura de código |

## 🚀 Inicio Rápido

### Prerequisitos

- Java 17 o superior
- Gradle 8.5+ (incluido via wrapper)

### Instalación y Ejecución

#### 1️⃣ Clonar el repositorio

```bash
git clone <repository-url>
cd MercadoLibre1
```

#### 2️⃣ Compilar el proyecto

```powershell
.\gradlew.bat clean build
```

#### 3️⃣ Ejecutar tests

```powershell
.\gradlew.bat test
```

#### 4️⃣ Ejecutar la aplicación

**Opción A: Ejecutar directamente**
```powershell
.\gradlew.bat bootRun
```

**Opción B: Ejecutar el JAR**
```powershell
.\gradlew.bat build
java -jar build\libs\MercadoLibre-1.0-SNAPSHOT.jar
```

#### 5️⃣ Acceder a la aplicación

- 🏠 **Home:** http://localhost:8080/
- 📚 **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- 📊 **Stats:** http://localhost:8080/stats

## 📡 Endpoints

### 🏠 GET `/`
Página de inicio con enlaces a la documentación.

**Response:** HTML

---

### 🧬 POST `/mutant`
Detecta si una secuencia de ADN pertenece a un mutante.

**Request Body:**
```json
{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Responses:**

| Código | Descripción | Ejemplo |
|--------|-------------|---------|
| `200 OK` | Es un mutante | `{"message": "Es un mutante"}` |
| `403 Forbidden` | No es un mutante | `{"message": "No es un mutante"}` |
| `400 Bad Request` | ADN inválido | `{"error": "ADN inválido..."}` |

**Validaciones:**
- Debe ser una matriz NxN (mínimo 4x4)
- Solo caracteres válidos: A, T, C, G
- Todas las filas deben tener la misma longitud

**Ejemplos con cURL:**

```bash
# Mutante (responde 200)
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'

# Humano (responde 403)
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{"dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"]}'
```

---

### 📊 GET `/stats`
Retorna estadísticas de las verificaciones de ADN.

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

**Descripción de campos:**
- `count_mutant_dna`: Cantidad de ADN mutantes detectados
- `count_human_dna`: Cantidad de ADN humanos detectados
- `ratio`: Proporción de mutantes vs humanos (0 si no hay humanos)

## 🐳 Docker

### Construir imagen

```powershell
docker build -t mutant-api:latest .
```

### Ejecutar contenedor

```powershell
docker run -e PORT=8080 -p 8080:8080 mutant-api:latest
```

### Docker Compose (opcional)

```yaml
version: '3.8'
services:
  mutant-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PORT=8080
```

## 🧪 Testing

### Ejecutar tests

```powershell
.\gradlew.bat test
```

### Generar reporte de cobertura

```powershell
.\gradlew.bat jacocoTestReport
```

El reporte HTML se genera en: `build/reports/jacoco/test/html/index.html`

### Ver todas las tareas disponibles

```powershell
.\gradlew.bat tasks
```

## 📁 Estructura del Proyecto

```
src/main/java/org/example/mercadolibre/
├── Launcher.java                      # Clase principal
├── controller/
│   └── MutantController.java          # Endpoints REST
├── service/
│   └── MutantService.java             # Lógica de detección
├── repository/
│   └── DnaRepository.java             # Acceso a datos
├── entity/
│   └── Dna.java                       # Entidad JPA
├── dto/
│   ├── DnaRequest.java                # DTO request
│   └── StatsResponse.java             # DTO response
└── config/
    └── SwaggerConfig.java             # Configuración Swagger
```

## ⚙️ Configuración

### Variables de Entorno

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `PORT` | 8080 | Puerto de la aplicación |
| `SPRING_PROFILES_ACTIVE` | default | Perfil de Spring Boot |

### application.properties

```properties
server.port=${PORT:8080}
spring.application.name=MercadoLibre
spring.datasource.url=jdbc:h2:mem:mutantdb
spring.h2.console.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🔍 Algoritmo de Detección

El algoritmo verifica si hay **más de una secuencia** de 4 letras iguales en cualquier dirección:

1. **Validación:** Matriz NxN (mínimo 4x4) con solo caracteres ATCG
2. **Búsqueda Horizontal:** Recorre cada fila buscando secuencias de 4
3. **Búsqueda Vertical:** Recorre cada columna buscando secuencias de 4
4. **Búsqueda Diagonal ↘:** Recorre diagonales descendentes
5. **Búsqueda Diagonal ↗:** Recorre diagonales ascendentes
6. **Resultado:** Si encuentra más de 1 secuencia → Es mutante

**Complejidad temporal:** O(N²) donde N es el tamaño de la matriz

## 📊 Persistencia

- **Base de datos:** H2 (en memoria para desarrollo)
- **Tabla:** `dna_records`
- **Campos:**
  - `id`: Long (PK, auto-generado)
  - `dna_hash`: String (SHA-256, único)
  - `is_mutant`: Boolean
  - `dna_sequence`: Text
  - `created_at`: DateTime

**Ventaja:** El hash SHA-256 evita analizar el mismo ADN múltiples veces.

## 📚 Documentación Adicional

- [CONVERSION-SUMMARY.md](CONVERSION-SUMMARY.md) - Resumen de conversión a Gradle
- [GRADLE-MIGRATION.md](GRADLE-MIGRATION.md) - Guía de migración
- [MAVEN-REMOVAL.md](MAVEN-REMOVAL.md) - Eliminación de Maven
- [MIGRATION-COMPLETE.md](MIGRATION-COMPLETE.md) - Migración completa
- [RESUMEN-COMENTARIOS.md](RESUMEN-COMENTARIOS.md) - Comentarios del código
- [VERIFICACION-FINAL.md](VERIFICACION-FINAL.md) - Verificación del proyecto

## 🚢 Despliegue en Render

El proyecto incluye configuración para despliegue en Render mediante `render.yaml`:

```yaml
services:
  - type: web
    name: mutant-detector-api
    env: docker
    dockerfilePath: ./Dockerfile
    buildCommand: ./gradlew build -x test
    startCommand: java -Dserver.port=$PORT -jar build/libs/MercadoLibre-1.0-SNAPSHOT.jar
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama feature (`git checkout -b feature/amazing-feature`)
3. Commit tus cambios (`git commit -m 'Add amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto fue desarrollado como parte del challenge de MercadoLibre.

## 👥 Autor

**MercadoLibre Challenge 2025**

---

⭐ Si te gustó el proyecto, dale una estrella!
