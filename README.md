# 🛒 API Bazar

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![JWT](https://img.shields.io/badge/Auth-JWT-blue)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Docker](https://img.shields.io/badge/Container-Docker-2496ED)

Sistema de gestión comercial desarrollado con **Spring Boot**, enfocado en la administración de usuarios, clientes, productos y ventas, incorporando autenticación JWT y autorización basada en roles y permisos.

---

## 🚀 Características Principales

### 🔐 Seguridad

* JWT Authentication
* Spring Security
* Roles dinámicos
* Permisos dinámicos
* Protección de endpoints mediante `@PreAuthorize`

### 👥 Gestión de Usuarios

* Registro de usuarios
* Inicio de sesión
* Actualización de credenciales
* Gestión de roles

### 🛍️ Gestión Comercial

* Administración de clientes
* Gestión de productos
* Control de inventario
* Registro de ventas
* Asociación de productos a ventas

### ⚙️ Arquitectura

* DTO Pattern
* Arquitectura por capas
* Manejo global de excepciones
* Persistencia con JPA/Hibernate
* Dockerización

---

## 🏗️ Arquitectura General

```text
┌──────────────┐
│  Controllers │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Services   │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│ Repositories │
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   MySQL DB   │
└──────────────┘
```

---

## 🔒 Flujo de Seguridad

```text
┌──────────┐
│  Login   │
└────┬─────┘
     │
     ▼
Validar Credenciales
     │
     ▼
Generar JWT
     │
     ▼
Cliente almacena Token
     │
     ▼
Authorization: Bearer TOKEN
     │
     ▼
JwtAuthenticationFilter
     │
     ▼
Acceso Autorizado
```

---

## 📂 Estructura del Proyecto

```text
src/main/java
│
└── com.bazar.apibazar
    │
    ├── controller      # Controladores REST
    ├── dto             # DTOs de entrada y salida
    ├── exception       # Excepciones y manejo global de errores
    ├── model           # Entidades JPA
    ├── repository      # Capa de persistencia
    ├── service         # Lógica de negocio
    │
    └── security
        │
        ├── config      # Configuración de seguridad
        └── jwt         # JWT, filtros y utilidades de autenticación
```

---

## 🛠️ Stack Tecnológico

| Tecnología         | Uso                      |
| ------------------ | ------------------------ |
| ☕ Java 17          | Lenguaje principal       |
| 🍃 Spring Boot     | Framework Backend        |
| 🔐 Spring Security | Seguridad                |
| 🎫 JWT             | Autenticación            |
| 🗄️ JPA/Hibernate  | Persistencia             |
| 🐬 MySQL           | Base de Datos            |
| 📦 Maven           | Gestión de dependencias  |
| 🐳 Docker          | Contenedorización        |
| ✨ Lombok           | Reducción de boilerplate |

---

## 🏗️ Diagrama de Dominio

```mermaid
graph TD

    User --> Role
    Role --> Permission

    User --> Cliente

    Cliente --> Venta

    Venta --> VentaProducto

    VentaProducto --> Producto
    
```

---

## 🗄️ Diagrama Entidad - Relación (ER)

```mermaid
erDiagram

    USER }o--o{ ROLE : tiene

    ROLE }o--o{ PERMISSION : contiene

    USER ||--|| CLIENTE : representa

    CLIENTE ||--o{ VENTA : realiza

    VENTA ||--o{ VENTA_PRODUCTO : contiene

    PRODUCTO ||--o{ VENTA_PRODUCTO : participa
```

---

### 📖 Relaciones Principales

- Un **USER** puede tener múltiples **ROLE**.
- Un **ROLE** puede contener múltiples **PERMISSION**.
- Un **USER** representa un único **CLIENTE**.
- Un **CLIENTE** puede realizar múltiples **VENTA**.
- Una **VENTA** puede contener múltiples productos mediante **VENTA_PRODUCTO**.
- **VENTA_PRODUCTO** permite almacenar la cantidad y el precio histórico de cada producto vendido.

---

## 📡 Módulos de la API

| Módulo | Descripción |
|---------|------------|
| 🔐 Autenticación | Registro e inicio de sesión mediante JWT |
| 👥 Usuarios | Gestión de usuarios, roles y credenciales |
| 🛒 Clientes | Administración de información de clientes |
| 📦 Productos | Gestión de inventario y productos |
| 💰 Ventas | Registro y consulta de ventas |
| 🛡️ Roles | Administración de roles |
| 🔑 Permisos | Administración de permisos |

---

# 📸 Ejemplos de Uso

A continuación se muestran algunas operaciones representativas de la API utilizando Postman.

## 🔐 Registro de Usuario

###  📷 Request

```http
POST http://localhost:8080/auth/register
```

<p align="center">
  <img src="docs/registro_usuario.png" width="800">
</p>

### ✅ Response

**Status:** `201 Created`

```json
{
  "id": 10,
  "username": "emanuel24",
  "idClient": 7,
  "active": true
}
```

## 🔑 Inicio de sesión

###  📷 Request

```http
POST http://localhost:8080/auth/login
```

<p align="center">
  <img src="docs/login.png" width="800">
</p>

### ✅ Response

**Status:** `200 OK`

```json
{
  "username": "emanuel24",
  "jwt": "eyJhbGciOiJIUzI1NiIsIn..."
}
```

## 💰 Registro de venta

###  📷 Request

```http
POST http://localhost:8080/ventas
```

#### Headers

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsIn...
```

<p align="center">
  <img src="docs/registro-venta.png" width="800">
</p>

### ✅ Response

**Status:** `201 Created`

```json
{
  "idVenta": 20,
  "fechaVenta": "2026-06-03",
  "cantidadTotalProductos": 1,
  "totalVenta": 500.00,
  "listProductos": [
    {
      "id": 4,
      "nombre": "Silla de escritorio",
      "cantidadComprada": 1,
      "precio": 500.00
    }
  ],
  "cliente": {
    "idCliente": 7,
    "active": true
  }
}
```

## 📦 Consulta de Productos

###  📷 Request

```http
GET http://localhost:8080/productos
```

### ✅ Response

**Status:** `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Laptop",
    "precio": 2500.00
  },
  {
    "id": 2,
    "nombre": "Mouse",
    "precio": 50.00
  },
  {
    "id": 4,
    "nombre": "Silla de escritorio",
    "precio": 500.00
  }
]
```

---

## ⚠️ Manejo Global de Excepciones

Implementado mediante:

```java
@RestControllerAdvice
```

### Excepciones Personalizadas

| Excepción                          |
| ---------------------------------- |
| ClienteNotFoundException           |
| UserNotFoundException              |
| UsernameAlreadyExistsException     |
| ProductoNotFoundException          |
| ProductoStockInsuficienteException |
| RoleNotFoundException              |
| InvalidRoleAssignmentException     |
| PermissionNotFoundException        |
| VentaNotFoundException             |
| VentaCanceledException             |
| UnauthorizedOperationException     |


### Ejemplo de Respuesta

```json
{
  "timestamp": "2026-05-20T18:25:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró producto con id: 77"
}
```

---

## ⚙️ Variables de Entorno

```env
DB_URL=jdbc:mysql://localhost:3306/api_bazar
DB_USER=user
DB_PASSWORD=password

JWT_SECRET=secret_key
JWT_USER_GENERATOR=AUTH0JWT
```

---

## 🚀 Ejecución Local

### Clonar repositorio

```bash
git clone https://github.com/Emanuel2407/Api_Bazar
cd api-bazar
```

### Compilar

```bash
mvn clean install
```

### Ejecutar

```bash
mvn spring-boot:run
```

Aplicación disponible en:

```text
http://localhost:8080
```

---

## 🐳 Docker

Levanta la aplicación junto con la base de datos configurada en Docker Compose.

### Construir Imagen

```bash
docker build -t api-bazar .
```

### Levantar Contenedores

```bash
docker compose up -d
```

---

## 🎯 Buenas Prácticas Implementadas

✅ Principio de Responsabilidad Única

✅ Inyección de Dependencias

✅ DTO Pattern

✅ Soft Delete

✅ Bean Validation

✅ JWT Authentication

✅ Role Based Access Control (RBAC)

✅ Ownership

✅ Manejo Global de Excepciones

✅ Configuración mediante Variables de Entorno

✅ Separación por Capas


---

## 👨‍💻 Autor

**Emanuel**

Backend Developer | Java • Spring Boot • Security • JPA/Hibernate

---

## 📄 Licencia

Proyecto desarrollado con fines educativos y de aprendizaje profesional.
