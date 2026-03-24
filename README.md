# Todo List API

## 🚀 Introducción del proyecto

**Todo List API** es una API REST construida con **Java + Spring Boot** para gestionar tareas y subtareas por usuario.

- **Caso de uso**: autenticación + gestión de tareas de forma segura (multi‑usuario).
- **Aislamiento de datos**: cada request autenticada opera sobre recursos del **usuario del token**.
- **Documentación**: OpenAPI/Swagger UI integrado.

<!--MODIFICAR 🌐 Prueba la API online: https://enlace-a-proyecto -->

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje**: Java 17
- **Framework backend**: Spring Boot 4.0.1
  - `spring-boot-starter-web` (REST)
  - `spring-boot-starter-data-jpa` (persistencia con JPA/Hibernate)
  - `spring-boot-starter-validation` (validaciones con Jakarta Validation)
  - `spring-boot-starter-security` (seguridad)
- **Seguridad y autenticación**:
  - Spring Security
  - JWT con **JJWT (io.jsonwebtoken)** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **Documentación de la API**:
  - **springdoc-openapi-starter-webmvc-ui** (Swagger UI / OpenAPI 3)
- **Base de datos**:
  - **PostgreSQL** (entorno de desarrollo y producción)
- **Build tool**: Maven (`pom.xml`)
- **ORM**: Hibernate (vía Spring Data JPA)
- **Mapeo y utilidades**:
  - Lombok (`@Getter`, `@Setter`, `@Data`, `@RequiredArgsConstructor`, etc.)
- **Testing**:
  - `spring-boot-starter-test`
  - Tests de servicios, mappers, seguridad y JWT

---

## ✨ Características

- **Auth**: registro, login, refresh de tokens y logout (con refresh token).
- **Tasks**: CRUD de tareas con paginación y búsqueda por nombre.
- **TaskItems**: CRUD de subtareas por tarea con paginación.
- **Users**: endpoints “me” para consultar/eliminar el usuario autenticado.
- **Seguridad**: Spring Security + JWT (sesión stateless).
- **Robustez**: validaciones en DTOs + manejo global de errores (`ErrorResponse`).
- **Contrato**: OpenAPI/Swagger + esquema de seguridad `bearer` JWT.

---

## 📁 Estructura del proyecto

Estructura principal bajo `src/main/java/com/rubiojdev/todolist`:


```bash
auth/
 ├── controllers/   # AuthController
 ├── services/      # AuthService, RefreshTokenService
 ├── entities/      # RefreshToken
 ├── dtos/          # LoginRequest, RegisterRequest, AuthResponse, RefreshTokenRequest
 ├── repositories/  # RefreshTokenRepository
 ├── mappers/       # AuthMapper, AuthMapperImpl
 └── docs/          # AuthApiDocs (anotaciones OpenAPI)

users/
 ├── controllers/   # UserController
 ├── services/      # UserService, UserServiceImpl
 ├── entities/      # User
 ├── dtos/          # UserCreateDto, UserResponseDto
 ├── repositories/  # UserRepository
 ├── mappers/       # UserMapper, UserMapperImpl
 └── docs/          # UserApiDocs

tasks/
 ├── controllers/   # TaskController
 ├── services/      # TaskService, TaskServiceImpl
 ├── entities/      # Task
 ├── dtos/          # TaskCreateDto, TaskUpdateDto, TaskResponseDto, TaskWithItemsResponseDto
 ├── repositories/  # TaskRepository
 ├── mappers/       # TaskMapper, TaskMapperImpl
 └── docs/          # TaskApiDocs

taskitems/
 ├── controllers/   # TaskItemController
 ├── services/      # TaskItemService, TaskItemServiceImpl
 ├── entities/      # TaskItem
 ├── dtos/          # TaskItemCreateDto, TaskItemUpdateDto, TaskItemResponseDto
 ├── repositories/  # TaskItemRepository
 ├── mappers/       # TaskItemMapper, TaskItemMapperImpl
 └── docs/          # TaskItemsApiDocs

security/
 ├── config/        # SecurityConfig, PasswordEncoderConfig
 ├── filter/        # JwtAuthenticationFilter
 ├── jwt/           # JwtService
 ├── model/         # CustomUserDetails
 ├── service/       # CustomUserDetailsService
 └── handler/       # CustomAuthenticationEntryPoint, CustomAccessDeniedHandler

config/             # SwaggerConfig, # JacksonConfig

shared/
 ├── dto/           # PageResponse, ErrorResponse
 └── exceptions/    # GlobalExceptionHandler, DuplicateResourceException, EntityNotFoundException, InvalidTokenException

```

---

## 🏗️ Arquitectura

La API sigue una arquitectura por capas típica en Spring Boot:

`Controller → Service → Repository → Entity`

- **Controllers**: exponen los endpoints REST y manejan las solicitudes/ respuestas HTTP.
- **Services**: contienen la lógica de negocio (gestión de usuarios, tareas, tokens, etc.).
- **Repositories**: interfaces de acceso a datos basadas en Spring Data JPA.
- **Entities**: representan las tablas de la base de datos (`User`, `Task`, `TaskItem`, `RefreshToken`).
- **DTOs**: encapsulan datos de entrada y salida, con anotaciones de validación.
- **Capa de seguridad**: intercepta las peticiones, valida el token JWT y establece el contexto de seguridad.

---

## ⚙️ Instalación

### Prerrequisitos

- **Java 17** instalado.
- **Maven** instalado.
- **PostgreSQL** en ejecución (para perfiles `dev`/`prod`).

### Configuración por entorno

La aplicación usa `server.servlet.context-path=/api` y corre en el puerto `8080` (por defecto).

| Entorno | Archivo | Base de datos | Notas |
|---|---|---|---|
| **dev** | `src/main/resources/application-dev.properties` | PostgreSQL | `ddl-auto=update`, `jwt.secret` configurado en el archivo |
| **prod** | `src/main/resources/application-prod.properties` | PostgreSQL | `ddl-auto=validate`, usa variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`) |
| **test** | `src/test/resources/application-test.properties` | H2 in‑memory | `ddl-auto=create-drop`, consola H2 habilitada, `jwt.secret` configurado en el archivo |

> Nota: en `dev` y `test` existe una `jwt.secret` en el repositorio. Para un despliegue real, se recomienda externalizar el secreto como variable de entorno (como ya está planteado en `prod`).

### Ejecutar la aplicación

1. Clonar el repositorio

```bash
git clone https://github.com/RubiojDev/todo-list-api.git
```

2. Configurar PostgreSQL en `application-dev.properties`

3. Desde la raíz del proyecto ejecutarlo

```bash
mvn spring-boot:run
```

Por defecto la API estará disponible en:

- `http://localhost:8080/api`

---

## 🧪 Ejecución de tests

Los tests utilizan:

- **Base de datos H2 in‑memory** configurada en `application-test.properties`.
  - `spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1`
  - `spring.jpa.hibernate.ddl-auto=create-drop`
- **Spring Boot Test** y JUnit.

Se cubren, entre otros:

- `TodolistApplicationTests` (contexto de la aplicación).
- Tests de servicios:
  - `AuthServiceTest`, `RefreshTokenServiceImplTest`
  - `UserServiceImplTest`, `TaskServiceImplTest`, `TaskItemServiceImplTest`
- Tests de seguridad:
  - `JwtServiceTest`, `JwtAuthenticationFilterTest`, `CustomUserDetailsServiceTest`
- Tests de mappers:
  - `AuthMapperImplTest`, `UserMapperImplTest`, `TaskMapperImplTest`, `TaskItemMapperImplTest`

Para ejecutar la batería de tests:

```bash
mvn test
```

---

## 🔐 Autenticación

La autenticación se realiza mediante **JWT** usando Spring Security.

- Configuración principal en `SecurityConfig`:
  - Deshabilita CSRF.
  - Configura sesiones **STATELESS**.
  - Permite libre acceso a:
    - `/auth/**`
    - `/v3/api-docs/**`
    - `/swagger-ui/**`
    - `/swagger-ui.html`
  - Requiere autenticación para el resto de endpoints.
  - Registra el filtro `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`.
  - Define manejadores personalizados para errores:
    - `CustomAuthenticationEntryPoint`
    - `CustomAccessDeniedHandler`

- El filtro `JwtAuthenticationFilter`:
  - Lee el encabezado `Authorization`.
  - Espera el formato `Bearer <token>`.
  - Extrae el email del usuario usando `JwtService`.
  - Carga el usuario mediante `UserDetailsService`.
  - Valida el token y, si es correcto, establece la autenticación en el `SecurityContext`.

- El servicio `JwtService`:
  - Genera tokens JWT firmados con **HS256**.
  - Usa el secreto `jwt.secret` (Base64) desde las propiedades.
  - Tiempo de expiración aproximado: 15 minutos.
  - Extrae el `username` y valida expiración/integridad del token.

Todos los endpoints protegidos utilizan el esquema de seguridad definido en OpenAPI (`bearer` JWT en el header `Authorization`).

---

## 📌 Principales endpoints de la API

> Todas las rutas están prefijadas por el contexto `/api` configurado en `server.servlet.context-path`.

### Módulo Auth (`/api/auth`)

- **POST** `/auth/login` → `AuthResponse`
- **POST** `/auth/register` → `AuthResponse` (HTTP 201)
- **POST** `/auth/refresh` → `AuthResponse`
- **POST** `/auth/logout` → (HTTP 204)

> Estos endpoints están expuestos sin autenticación previa, pero devuelven tokens para el resto de la API.

### Módulo Users (`/api/users`)

- **GET** `/users/me` → `UserResponseDto`
- **DELETE** `/users/me` → (HTTP 204)

> Requiere header `Authorization: Bearer <token>`.

### Módulo Tasks (`/api/tasks`)

- **GET** `/tasks` → `PageResponse<TaskResponseDto>`
  - Query: `page` (min 0), `size` (min 1, max 20)
- **GET** `/tasks/{id}` → `TaskWithItemsResponseDto`
- **GET** `/tasks/name` → `PageResponse<TaskResponseDto>`
  - Query: `name` (obligatorio), `page`, `size`
- **POST** `/tasks` → `TaskResponseDto` (HTTP 201)
- **PATCH** `/tasks/{id}` → `TaskResponseDto`
- **DELETE** `/tasks/{id}` → (HTTP 204)

> Todos los endpoints de tareas requieren autenticación JWT.

### Módulo TaskItems (`/api/tasks/{taskId}/items`)

- **GET** `/tasks/{taskId}/items` → `PageResponse<TaskItemResponseDto>`
  - Query: `page` (min 0), `size` (min 1, max 20)
- **POST** `/tasks/{taskId}/items` → `TaskItemResponseDto` (HTTP 201)
- **PATCH** `/tasks/{taskId}/items/{id}` → `TaskItemResponseDto`
- **DELETE** `/tasks/{taskId}/items/{id}` → (HTTP 204)

> También requieren header `Authorization: Bearer <token>`.

---

## 📡 Ejemplos de uso

### Registro de usuario

**Request**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "juan",
  "email": "juan@example.com",
  "password": "MiPasswordSegura123"
}
```

**Response 201**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "d5b1e4c6-..."
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "MiPasswordSegura123"
}
```

Respuesta: igual formato que en el registro (`AuthResponse`).

### Obtener tareas paginadas

```http
GET /api/tasks?page=0&size=10
Authorization: Bearer <token>
Accept: application/json
```

**Response 200 (`PageResponse<TaskResponseDto>`)**

```json
{
  "content": [
    {
      "id": 1,
      "name": "Lista de compras",
      "updatedAt": "2024-01-01T10:00:00Z",
      "completed": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Crear una tarea

```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Estudiar Spring Boot"
}
```

**Response 201 (`TaskResponseDto`)**

```json
{
  "id": 2,
  "name": "Estudiar Spring Boot",
  "updatedAt": "2024-01-01T10:00:00Z",
  "completed": false
}
```

### Crear una subtarea

```http
POST /api/tasks/2/items
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Ver videotutoriales"
}
```

**Response 201 (ejemplo)**

```json
{
  "id": 5,
  "name": "Ver videotutoriales",
  "completed": false
}
```

### Obtener usuario autenticado

```http
GET /api/users/me
Authorization: Bearer <token>
Accept: application/json
```

**Response 200 (ejemplo)**

```json
{
  "id": 1,
  "username": "juan",
  "email": "juan@example.com",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

### Error estándar (`ErrorResponse`)

Cuando ocurre un error de validación o negocio, la API devuelve un cuerpo consistente:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Parametros invalidos",
  "path": "/api/tasks",
  "timestamp": "2024-01-01T10:00:00Z",
  "fields": {
    "name": "Se necesita el nombre de la tarea"
  }
}
```

---

## 📋 Validaciones

Las validaciones se definen principalmente en los DTOs mediante **Jakarta Validation**:

- **Auth**
  - `LoginRequest`:
    - `email`: `@Email`, `@NotBlank`
    - `password`: `@NotBlank`
  - `RegisterRequest`:
    - `username`: `@NotBlank`
    - `email`: `@Email`, `@NotBlank`
    - `password`: `@NotBlank`

- **Tasks**
  - `TaskCreateDto`:
    - `name`: `@NotBlank(message = "Se necesita el nombre de la tarea")`
  - `TaskUpdateDto`:
    - `name`: opcional
    - `completed`: opcional

- **TaskItems**
  - `TaskItemCreateDto`:
    - `name`: `@NotBlank(message = "El nombre de la subtarea es obligatorio")`
  - `TaskItemUpdateDto`:
    - `name`: opcional
    - `completed`: opcional

- **Parámetros de paginación**
  - En controladores (`TaskController`, `TaskItemController`) se usan:
    - `@Min(0)` para `page`
    - `@Min(1)` y `@Max(20)` para `size`

Los errores de validación y excepciones de negocio se centralizan en `GlobalExceptionHandler`, devolviendo un `ErrorResponse` consistente.

---

## 📄 Documentación Swagger / OpenAPI

Accede a la documentación automática de los endpoints:

Documentación local: `http://localhost:8080/api/swagger-ui/index.html`

<!-- Documentación producción: `http://SERVER/api/swagger-ui/index.html` MODIFICAR-->

Incluye todos los endpoints con ejemplos de request y response

---

## 🚀 Postman Collection

Se ha preparado una colección de Postman que incluye todos los endpoints configurados con sus respectivos cuerpos de solicitud y variables de entorno.

Cómo utilizarla:
Descarga el archivo: Puedes encontrar el JSON de la colección en la carpeta /resources/postman del repositorio o descargarlo directamente desde el siguiente enlace:

🔗 [PostmanCollection](/resources/postman/API%20Todo%20List.postman_collection.json)

Importar: Abre Postman, haz clic en Import y arrastra el archivo JSON.

Configurar el Entorno: La colección utiliza una variable {{base_url}}. Asegúrate de configurar tu entorno local (ej. `http://localhost:8080/api`) para que las peticiones funcionen correctamente.

Flujo de Auth: La colección está configurada para capturar automáticamente el accessToken tras el login y guardarlo en una variable de entorno, por lo que no tendrás que copiar y pegar el token manualmente en cada petición.

---

## 🧠 Notas de implementación

- **Diseño y separación de responsabilidades**: `Controller → Service → Repository → Entity`, con DTOs para entrada/salida.
- **Contrato OpenAPI desacoplado**: los controladores implementan interfaces `*ApiDocs` con anotaciones Swagger, mejorando legibilidad y mantenibilidad.
- **Persistencia**: JPA/Hibernate con relaciones:
  - `User` 1‑N `Task`
  - `Task` 1‑N `TaskItem`
- **Integridad de datos**: restricción de unicidad en `Task` para evitar duplicados por usuario (`user_id` + `name`).
- **Auditoría simple**: timestamps y flags (`createdAt`, `updatedAt`, `completed`) con callbacks JPA (`@PrePersist`, `@PreUpdate`).
- **Seguridad stateless**: JWT + filtro por request, sin sesiones en servidor.

---

## 🙋 Autor

**Jesus Rubio**

- **Portfolio**: https://rubiojdev.github.io  
- **GitHub**: https://github.com/RubiojDev  
- **GitBook (Documentación Completa)**: https://rubiojdev.gitbook.io/todo-list-api/
- **Email**: `jesusantoniorubiot@gmail.com`

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.
Esto significa que eres libre de usar, copiar, modificar, fusionar, publicar, distribuir y sublicenciar el código, siempre y cuando se incluya el aviso de copyright original. Es una licencia permisiva ideal para proyectos educativos y de código abierto.

