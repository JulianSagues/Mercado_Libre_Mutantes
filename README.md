# 🧬 Mutant Detection API - MercadoLibre Challenge

API REST para detectar si un humano es mutante basándose en su secuencia de ADN.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Tecnologías](#-tecnologías)
- [Instalación](#-instalación)
- [Uso](#-uso)
- [Endpoints](#-endpoints)
- [Algoritmo](#-algoritmo)
- [Testing](#-testing)
- [Swagger UI](#-swagger-ui)
- [Base de Datos](#-base-de-datos)

## 🎯 Descripción

Magneto quiere reclutar la mayor cantidad de mutantes para poder luchar contra los X-Men. Esta API detecta si un humano es mutante mediante el análisis de su secuencia de ADN.

Un humano es considerado **mutante** si se encuentra **más de una secuencia de cuatro letras iguales**, de forma oblicua, horizontal o vertical.

### Ejemplo de ADN Mutante:

```
A T G C G A
C A G T G C
T T A T G T
A G A A G G
C C C C T A  ← Secuencia horizontal
T C A C T G
```

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (base de datos en memoria)
- **Maven** (gestión de dependencias)
- **JUnit 5 + Mockito** (testing)
- **Swagger/OpenAPI 3** (documentación)

## 📦 Instalación

### Prerrequisitos

- Java 17 o superior
- Maven 3.8+ (o usar el Maven Wrapper incluido)

### Clonar e Iniciar

```bash
# Clonar el repositorio
git clone <repository-url>
cd MercadoLibre

# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 🚀 Uso

### Verificar si un ADN es mutante

**Request:**
```bash
POST http://localhost:8080/mutant
Content-Type: application/json

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

**Response:**
- **200 OK** - Es mutante
```json
{"message": "Es un mutante"}
```

- **403 Forbidden** - No es mutante
```json
{"message": "No es un mutante"}
```

- **400 Bad Request** - ADN inválido
```json
{"error": "ADN inválido: debe ser una matriz NxN con solo caracteres A, T, C, G"}
```

### Obtener estadísticas

**Request:**
```bash
GET http://localhost:8080/stats
```

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

## 📡 Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/mutant` | Detecta si un ADN es mutante |
| GET | `/stats` | Obtiene estadísticas de verificaciones |

## 🧮 Algoritmo

El algoritmo detecta secuencias de 4 letras iguales en las siguientes direcciones:

### 1. Horizontal (→)
```
A A A A G T
```

### 2. Vertical (↓)
```
A . . .
A . . .
A . . .
A . . .
```

### 3. Diagonal Descendente (↘)
```
A . . . . .
. A . . . .
. . A . . .
. . . A . .
```

### 4. Diagonal Ascendente (↗)
```
. . . A . .
. . A . . .
. A . . . .
A . . . . .
```

### Optimización

- **Early Exit**: El algoritmo se detiene cuando encuentra más de 1 secuencia (no necesita seguir buscando).
- **Deduplicación**: Usa SHA-256 hash para evitar procesar el mismo ADN múltiples veces.
- **Complejidad**: O(N²) donde N es el tamaño de la matriz.

## 🧪 Testing

El proyecto incluye 14 tests unitarios que cubren:

- ✅ Detección de mutantes con diferentes tipos de secuencias
- ✅ Detección de humanos (no mutantes)
- ✅ Validaciones de entrada (ADN nulo, matriz pequeña, caracteres inválidos)
- ✅ Integración con base de datos (guardado y recuperación)
- ✅ Cálculo de estadísticas

### Ejecutar Tests

```bash
./mvnw test
```

**Resultado esperado:**
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

### Cobertura de Tests

Los tests cubren:
- Casos de éxito (mutante detectado)
- Casos de fallo (humano detectado)
- Casos edge (matrices pequeñas, null, caracteres inválidos)
- Persistencia en base de datos
- Estadísticas

## 📚 Swagger UI

La documentación interactiva de la API está disponible en:

```
http://localhost:8080/swagger-ui.html
```

También puedes ver la especificación OpenAPI en:

```
http://localhost:8080/api-docs
```

## 💾 Base de Datos

### H2 Database (En Memoria)

La aplicación usa H2, una base de datos en memoria para desarrollo y pruebas.

**Consola H2:**
```
http://localhost:8080/h2-console
```

**Credenciales:**
- JDBC URL: `jdbc:h2:mem:mutantdb`
- Username: `sa`
- Password: *(dejar vacío)*

### Esquema de Base de Datos

**Tabla: `dna_records`**

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | Primary Key (auto-increment) |
| dna_hash | VARCHAR(255) | Hash SHA-256 del ADN (único) |
| is_mutant | BOOLEAN | true si es mutante, false si no |
| dna_sequence | TEXT | Secuencia de ADN original (JSON) |
| created_at | TIMESTAMP | Fecha de creación |

## 🏗️ Arquitectura

```
src/main/java/org/example/mercadolibre/
├── Launcher.java                 # Punto de entrada de Spring Boot
├── controller/
│   └── MutantController.java     # REST endpoints
├── dto/
│   ├── DnaRequest.java           # Request DTO
│   └── StatsResponse.java        # Response DTO
├── service/
│   └── MutantService.java        # Lógica del algoritmo
├── repository/
│   └── DnaRepository.java        # Acceso a datos (JPA)
├── entity/
│   └── Dna.java                  # Entidad JPA
└── config/
    └── SwaggerConfig.java        # Configuración de Swagger
```

## 🎨 Ejemplos de Uso con PowerShell

### Ejemplo 1: ADN Mutante
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/mutant" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"dna":["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"]}'
```

### Ejemplo 2: ADN Humano
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/mutant" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"dna":["ATGCGA","CAGTGC","TTATTT","AGACGG","GCGTCA","TCACTG"]}'
```

### Ejemplo 3: Obtener Estadísticas
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/stats" -Method GET
```

## 📝 Validaciones

La API valida:

- ✅ El ADN no puede ser nulo
- ✅ Debe tener al menos 4 secuencias (4x4 mínimo)
- ✅ Debe ser una matriz cuadrada (NxN)
- ✅ Solo puede contener las letras: A, T, C, G
- ✅ Todas las filas deben tener el mismo tamaño

## 🔒 Características de Seguridad

- **Deduplicación**: No se procesan ADNs duplicados (usando hash SHA-256)
- **Validación de entrada**: Rechaza datos inválidos antes de procesarlos
- **Manejo de errores**: Retorna códigos HTTP apropiados y mensajes descriptivos

## 📊 Performance

- **Complejidad temporal**: O(N²) donde N es el tamaño de la matriz
- **Complejidad espacial**: O(1) - no usa estructuras auxiliares grandes
- **Optimización**: Early exit cuando encuentra >1 secuencia

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto fue creado como parte del challenge de MercadoLibre.

## ✨ Autor

Desarrollado para el challenge de MercadoLibre - 2025

---

**¿Necesitas ayuda?** Abre un issue en el repositorio o contacta al equipo de desarrollo.

