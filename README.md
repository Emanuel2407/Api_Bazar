# 🛒 API Bazar

Una API REST construida con **Spring Boot** para manejar operaciones críticas de venta, gestión de inventarios y clientes. Diseñada siguiendo buenas prácticas de desarrollo backend, con persistencia en base de datos y arquitectura limpia.

---

## ✨ Características

- ⚙️ **API RESTful con Spring Boot**
- 🗄️ **Persistencia con JPA / Hibernate**
- 🔄 **Conexión a bases de datos (H2 y MySQL)**
- 📦 **Gestión de entidades del sistema**
- 🧪 **Soporte para testing**
- 🐳 **Contenedorización con Docker**

---

## 🧱 Tecnologías utilizadas

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL
- H2 Database
- Lombok
- Maven
- Docker

---

## 📁 Estructura del Proyecto
```text
apibazar/
├── src/
│   ├── main/
│   │   ├── java/        # Lógica de negocio y controladores
│   │   └── resources/   # Archivos de propiedades y SQL
│   └── test/            # Pruebas unitarias y de integración
├── Dockerfile           # Definición de imagen para el servicio
├── docker-compose.yml   # Orquestación de contenedores (App + DB)
├── pom.xml              # Gestión de dependencias Maven
├── .env.example         # Plantilla de variables de entorno
└── README.md            # Documentación del proyecto
```

---

## 🚀 Cómo ejecutar el proyecto

### 🔧 Prerrequisitos

- Java 17+
- Maven
- Docker (opcional)

### 📥 Clonar repositorio
```bash
git clone https://github.com/Emanuel2407/Api_Bazar
cd apibazar
```

### ⚙️ Definir variables de entorno

Puedes basarte en el archivo `.env.example`:
```bash
# Credenciales de base de datos
DB_URL=
DB_USERNAME=
DB_PASSWORD=
```

### ▶️ Ejecución local
```bash
./mvnw spring-boot:run
```

### 🐳 Ejecución con Docker
```bash
docker-compose up --build
```

---

## 🔌 Base de datos

El proyecto soporta:

- 🧪 **H2 (en memoria)** → Ideal para desarrollo
- 🐬 **MySQL** → Para entornos más reales

Configurable desde `application.properties`.

---

## 📡 Endpoints
```http
GET    /productos/                  # Traer todos los productos
GET    /productos/{id}              # Traer un producto por su ID
POST   /ventas/                     # Traer todas las ventas
PATCH  /clientes/add-ventas/{id}    # Asignar venta a cliente
GET    /ventas/productos/{id}       # Traer productos de una venta
PUT    /productos/{id}              # Actualizar totalmente un producto
PATCH  /productos/{id}              # Actualizar parcialmente un producto
DELETE /clientes/{id}               # Eliminar cliente por su ID
```

---

## ⚠️ Manejo de errores

Se implementa un manejador global de excepciones mediante `@RestControllerAdvice`.

### Excepciones manejadas:

| Excepción | Status |
|---|---|
| `ClienteNotFoundException` | `404 NOT_FOUND` |
| `ProductoNotFoundException` | `404 NOT_FOUND` |
| `VentaNotFoundException` | `404 NOT_FOUND` |
| `ProductoStockInsuficienteException` | `409 CONFLICT` |

### Estructura de respuesta de error:
```json
{
  "timestamp": "2026-03-28T12:00:00",
  "status": 404,
  "error": "Not Found",
  "mensaje": "Cliente no encontrado con id: 1"
}
```

---

## 🧪 Testing
```bash
./mvnw test
```

---

## 📦 Build
```bash
./mvnw clean package
```

---

## 🧠 Buenas prácticas aplicadas

- Separación por capas (Controller, Service, Repository)
- Uso de DTOs para entrada y salida de datos
- Manejo global de excepciones con `@RestControllerAdvice`
- Configuración desacoplada

---

## 💻 Autor

**Desarrollado por Emanuel Atencia 🚀**

---

## 📄 Licencia

Este proyecto es de uso académico / personal.