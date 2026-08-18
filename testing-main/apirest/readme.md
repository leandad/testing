# API REST - Sistema de Alerta Financiera Temprana

Proyecto backend en Spring Boot para gestionar usuarios, gastos financieros y evaluación de riesgo financiero. La solución está pensada para calcular señales tempranas de salud económica, clasificar perfiles financieros y facilitar la gestión de transacciones asociadas a cada cliente.

## Descripción general

La API permite:

- Registrar y consultar usuarios financieros.
- Registrar y consultar gastos asociados a cada usuario.
- Evaluar indicadores de salud financiera como ahorro neto, DTI, gastos esenciales y meses de supervivencia.
- Filtrar usuarios por perfil financiero.
- Integrar un cálculo adicional con un servicio Python para el análisis predictivo.

## Objetivo

Brindar una herramienta de gestión financiera para visualizar de forma rápida y estructurada la situación económica de cada usuario, detectando señales de riesgo y permitiendo una mejor toma de decisiones.

## Funcionalidades principales

- Gestión de usuarios y clientes financieros.
- Registro y consulta de gastos por usuario.
- Cálculo de ratios financieros y métricas clave.
- Perfilamiento financiero por riesgo.
- Persistencia relacional en MySQL.
- Arquitectura REST con Spring Boot.
- Despliegue operativo en una instancia OCI con acceso público en el puerto 8080.

## Stack tecnológico

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL 8
- Maven
- OCI (Oracle Cloud Infrastructure) VM
- systemd para servicio del backend

## Estructura del proyecto

```text
apirest/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── team08/apirest/
│   │   │       ├── controllers/
│   │   │       │   ├── GastoController.java
│   │   │       │   └── UsuarioController.java
│   │   │       ├── models/
│   │   │       │   ├── GastoModel.java
│   │   │       │   └── UsuarioModel.java
│   │   │       ├── repositories/
│   │   │       │   ├── GastoRepository.java
│   │   │       │   └── UsuarioRepository.java
│   │   │       ├── services/
│   │   │       │   ├── GastoService.java
│   │   │       │   └── UsuarioService.java
│   │   │       ├── ApirestApplication.java
│   │   │       └── ServletInitializer.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/team08/apirest/ApirestApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── readme.md
└── target/
```

## Requisitos de ejecución

- Java 21
- Maven
- MySQL Server
- Acceso SSH a la infraestructura OCI si se desea desplegar en servidor

## Configuración local

Archivo principal:

```properties
spring.application.name=apirest
server.address=0.0.0.0
server.port=8080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/apirest?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### Ejecutar localmente

```bash
cd apirest
./mvnw clean package -DskipTests
java -jar target/apirest-0.0.1-SNAPSHOT.war --server.port=8080
```

## Despliegue en OCI

El backend quedó configurado y ejecutándose en una instancia OCI con acceso público en el puerto 8080.

### Datos de despliegue verificados

- IP pública: 146.181.60.43
- Usuario: ubuntu
- Puerto: 8080
- URL base: http://146.181.60.43:8080

### Pasos de despliegue usados

1. Instalar Java 21 y Maven en la VM.
2. Instalar y preparar MySQL.
3. Crear la base de datos `apirest`.
4. Ajustar la autenticación de MySQL para permitir `root@localhost` con `mysql_native_password`.
5. Compilar el proyecto con Maven.
6. Subir el artefacto WAR a la VM.
7. Crear un servicio `systemd` para arrancar la aplicación automáticamente.

### Servicio systemd

```ini
[Unit]
Description=API REST Backend
After=network.target mysql.service

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu
ExecStart=/usr/lib/jvm/java-21-openjdk-amd64/bin/java -jar /home/ubuntu/apirest.war --server.port=8080
Restart=always
RestartSec=10
Environment=SPRING_DATASOURCE_URL=jdbc:mysql://127.0.0.1:3306/apirest?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
Environment=SPRING_DATASOURCE_USERNAME=root
Environment=SPRING_DATASOURCE_PASSWORD=root
StandardOutput=append:/home/ubuntu/apirest.log
StandardError=append:/home/ubuntu/apirest.log

[Install]
WantedBy=multi-user.target
```

### Comandos de gestión del servicio

```bash
sudo systemctl status apirest
sudo systemctl restart apirest
sudo journalctl -u apirest -f
```

## Endpoints de la API REST

### Módulo de gastos

#### 1) Listar todos los gastos

```http
GET /api/gastos
```

#### 2) Registrar un gasto

```http
POST /api/gastos
Content-Type: application/json
```

Ejemplo de body:

```json
{
  "nombreTienda": "Supermercado",
  "subcategoria": "Alimentos",
  "monto": 1500.5,
  "metodoPago": "Tarjeta",
  "esencial": true,
  "categoriaPrincipal": "Hogar",
  "usuario": {
    "id": 1
  }
}
```

#### 3) Obtener gastos por cliente

```http
GET /api/gastos/cliente/{idCliente}
```

#### 4) Obtener gasto por ID

```http
GET /api/gastos/{id}
```

#### 5) Eliminar gasto

```http
DELETE /api/gastos/{id}
```

### Módulo de usuarios

#### 1) Listar usuarios

```http
GET /api/usuarios
```

#### 2) Registrar usuario

```http
POST /api/usuarios
Content-Type: application/json
```

Ejemplo de ejemplo:

```json
{
  "nombre": "Ana",
  "password": "123456",
  "email": "ana@email.com",
  "ingresoMensualFijo": 2500000,
  "ingresoMensualVariable": 500000,
  "ingresoMensual": 3000000,
  "gastosEsencialesMensuales": 1200000,
  "gastosNoEsencialesMensuales": 400000,
  "gastosTotalesDelMes": 1600000,
  "cuotasMensualesDeuda": 300000,
  "ahorroMensual": 700000,
  "ahorroTotal": 8000000,
  "perfilFinanciero": "Estable",
  "mesesSupervivencia": 8
}
```

#### 3) Obtener usuario por ID

```http
GET /api/usuarios/{id}
```

#### 4) Eliminar usuario

```http
DELETE /api/usuarios/{id}
```

#### 5) Filtrar usuarios por perfil financiero

```http
GET /api/usuarios/{perfilFinanciero}
```

#### 6) Filtrar usuarios con supervivencia > 0

```http
GET /api/usuarios/{meses_supervivencia}
```

## Observaciones del proyecto

- La aplicación utiliza JPA para mapear entidades y persistirlas en MySQL.
- La estructura de entidades incluye una relación entre `UsuarioModel` y `GastoModel`.
- La conexión a la base de datos se gestiona por Spring Boot HikariCP.
- La variable `spring.jpa.hibernate.ddl-auto=update` permite crear o actualizar tablas automáticamente al arrancar la aplicación.

## Problemas comunes y soluciones

### Error: Java version unsupported

Si el backend falla con `UnsupportedClassVersionError`, significa que se compiló con Java más reciente que la versión disponible en la VM.

Solución:

```bash
sudo apt-get install openjdk-21-jdk
```

Y compilar de nuevo con Java 21.

### Error: Access denied for user 'root'@'localhost'

Esto ocurre en MySQL cuando la cuenta root usa autenticación `auth_socket` y la aplicación intenta usar contraseña.

Solución:

```sql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
```

### Puerto 8080 no responde

Verificar:

```bash
sudo systemctl status apirest
sudo journalctl -u apirest -n 100 --no-pager
ss -lnt | grep 8080
```

## Equipo

Equipo 8 - Desarrollo backend y servicios financieros.

## Estado actual

El backend se encuentra operativo en OCI y escucha en el puerto 8080, listo para ser consumido por el frontend o por clientes HTTP externos.