# Sistema de Gestión de Alquileres — EquipaYa

Especificación técnica de arquitectura, modelo de clases, persistencia SQL y gestión de transacciones para el desarrollo del proyecto en Java y MySQL.

---

## 1. Estructura de Arquitectura y Capas

El proyecto sigue una arquitectura desacoplada en capas para garantizar la escalabilidad ante la adición de nuevos equipos (ej. Drones) o la migración a otros motores de base de datos (ej. PostgreSQL):

```text
src/
├── model/                  # Clases de dominio (Entidades, herencia y atributos)
├── dao/                    # Capa de datos y persistencia
│   ├── interfaces/         # Contratos/Interfaces de acceso a datos
│   └── impl/               # Implementaciones JDBC (MySQL)
├── business/               # Reglas de negocio, validaciones y control transaccional
├── service/                # Infraestructura técnica (Conexion.java / JDBC)
├── controller/             # Controladores de flujo entre vista y negocio (Gestores)
└── MainView.java           # Interfaz de usuario por consola (Vista)
```

---

## 2. Diseño y Script de Base de Datos (MySQL)

### Script SQL (`equipaya.sql`)
```sql
CREATE DATABASE IF NOT EXISTS equipaya;
USE equipaya;

-- 1. Tabla Clientes
CREATE TABLE IF NOT EXISTS clientes (
    documento VARCHAR(20) PRIMARY KEY,              -- DNI (8) o RUC (11) - Único por PK
    nombre_razon_social VARCHAR(150) NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    INDEX idx_cliente_doc (documento)                -- Requerimiento 5: Índice para búsquedas rápidas en mostrador
);

-- 2. Tabla Equipos
CREATE TABLE IF NOT EXISTS equipos (
    codigo VARCHAR(10) PRIMARY KEY,                  -- Código único del equipo
    descripcion VARCHAR(200) NOT NULL,
    tarifa_diaria DECIMAL(10, 2) NOT NULL CHECK (tarifa_diaria > 0),
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_equipo ENUM('CAMARA', 'ILUMINACION', 'AUDIO') NOT NULL -- Discriminador polimórfico
);

-- 3. Tabla Alquileres
CREATE TABLE IF NOT EXISTS alquileres (
    id_alquiler INT AUTO_INCREMENT PRIMARY KEY,
    documento_cliente VARCHAR(20) NOT NULL,
    codigo_equipo VARCHAR(10) NOT NULL,
    fecha_inicio DATE NOT NULL,
    dias INT NOT NULL CHECK (dias > 0),
    monto_cobrado DECIMAL(10, 2) NOT NULL CHECK (monto_cobrado >= 0),
    devuelto BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (documento_cliente) REFERENCES clientes(documento),
    FOREIGN KEY (codigo_equipo) REFERENCES equipos(codigo)
);
```

### Justificación de Estructura SQL:
* **Claves y Restricciones:** `documento` es PK única para DNI/RUC. `INDEX idx_cliente_doc` optimiza las consultas frecuentes en mostrador. `FOREIGN KEY` en `alquileres` garantiza la integridad referencial.
* **Discriminador Polimórfico:** `tipo_equipo ENUM(...)` permite que la capa DAO lea el tipo de equipo e instancie la subclase Java correspondiente (`Camara`, `Iluminacion`, `Audio`).

---

## 3. Especificación del Modelo de Clases (`model/`)

### **Clase Abstracta: `Equipo.java`**
* **Atributos:** `codigo` (String), `descripcion` (String), `tarifaDiaria` (double), `disponible` (boolean).
* **Métodos:** Getters, setters, constructores y método abstracto `public abstract double calcularMonto(int dias)`.

#### Subclases de `Equipo`:
1. **`Camara.java`**
   * **Regla de Cobro:** Tarifa por días más un 15% de seguro obligatorio.
2. **`Iluminacion.java`**
   * **Regla de Cobro:** Tarifa por días. Si la duración supera los 5 días, se aplica un 10% de descuento al total.
3. **`Audio.java`**
   * **Regla de Cobro:** Tarifa por días, garantizando un cobro mínimo de 2 días (si se alquila por 1 día, se calcula sobre la base de 2 días).

---

### **Clase Concreta: `Cliente.java`**
* **Atributos:** `documento` (String: DNI o RUC), `nombreRazonSocial` (String), `telefono` (String).
* **Métodos:** Getters, setters y constructores.

---

### **Clase Concreta: `Alquiler.java`**
* **Atributos:** `idAlquiler` (int), `cliente` (Cliente), `equipo` (Equipo), `fechaInicio` (LocalDate), `dias` (int), `montoCobrado` (double), `devuelto` (boolean).
* **Métodos:** Getters, setters y constructores.

---

## 4. Capa DAO: Interfaces e Implementaciones (`dao/`)

### A. Módulo Clientes
* **`ClienteDAO.java`** / **`ClienteDAOImpl.java`**
  * `buscarPorDocumento(String doc)`: Búsqueda indexada directa para el mostrador.
  * `buscarPorNombre(String texto)`: Búsqueda con `LIKE %texto%`, sanitizando entrada para evitar resultados masivos indeseados.

### B. Módulo Equipos
* **`EquipoDAO.java`** / **`EquipoDAOImpl.java`**
  * `buscarPorCodigo(String codigo)`: Retorna el equipo instanciando su subclase concreta según el discriminador `tipo_equipo`.
  * `actualizarEstadoDisponibilidad(String codigo, boolean disponible, Connection con)`: Actualiza el estado dentro de una transacción.
  * `mostrarFacturacionPorTipo()`: Agrupa el total facturado ordenado de forma estricta (`CAMARA`, `ILUMINACION`, `AUDIO`).

### C. Módulo Alquileres
* **`AlquilerDAO.java`** / **`AlquilerDAOImpl.java`**
  * `registrarAlquiler(Alquiler a, Connection con)`: Registra la transacción dentro del bloque atómico.
  * `marcarComoDevuelto(int idAlquiler, Connection con)`: Actualiza el estado a devuelto dentro del bloque atómico.
  * `mostrarPendientesDevolucion()`: Muestra los alquileres activos proyectando los nombres del cliente y descripción del equipo.

---

## 5. Lógica de Negocio y Control Transaccional (Corte de Luz)

### **Garantía de Atomicidad (`AlquilerBusiness.java`):**
Para dar respuesta al Requerimiento 1 (evitar inconsistencias tras un corte de luz o caída del sistema), **tanto la salida como la devolución del equipo se procesan en transacciones explícitas JDBC**:

1. **Al Registrar Alquiler:**
   * Se inicia la transacción con `conexion.setAutoCommit(false)`.
   * Se verifica disponibilidad (`equipo.isDisponible() == true`).
   * Se inserta el registro en `alquileres` y se actualiza `equipos` (`disponible = false`).
   * Se ejecuta `conexion.commit()`. Si hay algún error o corte, `conexion.rollback()` revierte ambos cambios.

2. **Al Procesar Devolución:**
   * Se inicia la transacción con `conexion.setAutoCommit(false)`.
   * Se actualiza el estado del contrato (`devuelto = true`).
   * Se libera el equipo físico (`disponible = true`).
   * Se ejecuta `conexion.commit()`. Si ocurre un fallo, `conexion.rollback()` deshace ambos cambios para no dejar el sistema en un estado inconsistente "al revés".

## Organización y Distribución del Trabajo en Equipo (4 Integrantes)

Para garantizar un desarrollo ágil y equitativo en la implementación en Java y en la redacción del entregable, la carga de trabajo se dividió por módulos funcionales y capas de la arquitectura:

---

### Integrante 1: Módulo de Clientes y Búsquedas en Mostrador
* **Desarrollo en Java:**
  * `Cliente.java` (Entidad del modelo).
  * `ClienteDAOImpl.java` (Implementación de consultas JDBC con `PreparedStatement`).
  * `ClienteBusiness.java` y `ClienteController.java`.
* **Sustentación y Entregables:**
  * **Requerimiento 5:** Explicación técnica del índice `idx_cliente_doc` para la optimización de lecturas repetidas en mostrador.
  * **Requerimiento 3:** Explicación de la sanitización de comodines en la búsqueda parcial por nombre con `LIKE %texto% ESCAPE '\'`.

---

### Integrante 2: Módulo de Equipos y Jerarquía POO
* **Desarrollo en Java:**
  * `Equipo.java` (Clase Abstracta).
  * `Camara.java`, `Iluminacion.java` y `Audio.java` (Subclases con cálculo de cobro polimórfico).
  * `EquipoDAOImpl.java` (Reconstrucción polimórfica vía `ENUM` y reporte de facturación).
  * `EquipoBusiness.java` y `EquipoController.java`.
* **Sustentación y Entregables:**
  * **Inciso b):** Justificación del modelo de clases (Clase Abstracta vs. Interfaz).
  * **Requerimiento 6:** Explicación de la escalabilidad para integrar el nuevo tipo de equipo `Dron` sin alterar el sistema existente (Principio Open/Closed).

---
### Integrante 3: Módulo de Alquileres, Pendientes e Integridad Referencial
* **Desarrollo en Java:**
  * `Alquiler.java` (Entidad del modelo).
  * `AlquilerDAOImpl.java` (Inserción, marcado de devolución y listados).
  * `AlquilerController.java`.
* **Sustentación y Entregables:**
  * **Inciso a):** Justificación del esquema de la base de datos, claves primarias (`PK`) e integridad referencial con claves foráneas (`FK`).
  * **Requerimiento 4:** Explicación de las consultas SQL avanzadas para los alquileres pendientes de devolución (uso de `JOIN` para proyectar nombres y descripciones en lugar de códigos).

---

### Integrante 4: Infraestructura JDBC, Transacciones Atómicas y Vista Principal
* **Desarrollo en Java:**
  * `Conexion.java` (Gestión de conexiones JDBC MySQL).
  * `AlquilerBusiness.java` (Implementación de transacciones atómicas con `setAutoCommit(false)`, `commit()` y `rollback()`).
  * `MainView.java` (Menú interactivo por consola con `Scanner`, `while` y `switch`).
* **Sustentación y Entregables:**
  * **Inciso c):** Justificación técnica del patrón de arquitectura N-Capas.
  * **Requerimientos 1 y 2:** Explicación de las transacciones explícitas JDBC para prevenir datos inconsistentes a causa de cortes de luz y bloqueo de equipos no disponibles.