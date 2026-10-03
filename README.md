# Goxu — API Backend

API REST del proyecto **Goxu**, la web de un restaurante de cocina tradicional asturiana. Proyecto en equipo del bootcamp de **Factoría F5 / Capgemini**.

## Índice

- [Descripción](#descripción)
- [Prototipo](#prototipo)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación y puesta en marcha](#instalación-y-puesta-en-marcha)
- [Testing](#testing)
- [Documentación](#documentación)
- [Autores](#autores)

## Descripción

Este repositorio contiene el **backend** de Goxu: la API que usa el [frontend](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-frontend) para gestionar:

- **Carta**: productos y categorías.
- **Pedidos**: creación, seguimiento y cambio de estado, con avisos por email.
- **Pagos**: pago simulado con tarjeta.
- **Eventos y ofertas** del restaurante.
- **Mensajes de contacto** del formulario "¿Hablamos?".
- **Usuarios y roles**: registro, login con JWT y acceso por rol (`CUSTOMER`, `KITCHEN`, `DELIVERY`, `ADMIN`).
- **Facturación**: informes de ventas en PDF.

Los diagramas, endpoints y decisiones de cada funcionalidad están en la [wiki](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki).

## Prototipo

El diseño de la aplicación (wireframes y vistas) está en [Figma](https://www.figma.com/design/XnqPKRgZj6eFMsjPowtYx8/GiaComo).

## Tecnologías

| Capa | Tecnología |
| ---- | ---------- |
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.8 |
| Persistencia | Spring Data JPA (Hibernate) |
| Base de datos | MySQL (en Docker) · H2 (perfil `h2`) |
| Seguridad | Spring Security + JWT |
| Email | Spring Mail (SMTP de Gmail) |
| PDF | Apache PDFBox |
| Tests | JUnit 5, Mockito, Testcontainers |
| Cobertura | JaCoCo |

## Requisitos

- **Java 21**
- **Docker Desktop**: levanta MySQL automáticamente al arrancar la aplicación.
- **Git**

No hace falta instalar Maven: el proyecto incluye el *wrapper* (`mvnw`).

## Instalación y puesta en marcha

1. **Clona el repositorio:**

```bash
   git clone https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend.git
   cd project-p5-digital-academy-team2-restaurant-backend
```

2. **Crea el archivo de credenciales** `src/main/resources/application-secrets.properties` con las credenciales del correo que envía los avisos de pedidos:

```properties
   spring.mail.username=tu-correo@gmail.com
   spring.mail.password=tu-contraseña-de-aplicación
```

   Este archivo está en el `.gitignore` y **no se sube al repositorio**. Si no existe, la aplicación arranca igualmente, pero no podrá enviar emails.

3. **Abre Docker Desktop** y espera a que esté en marcha.

4. **Arranca la aplicación:**

```bash
   ./mvnw spring-boot:run
```

   También puedes ejecutar `GoxuApplication.java` desde el IDE. Spring Boot levanta el contenedor de MySQL con `compose.yaml` y carga los datos iniciales de `data.sql`.

5. La API queda disponible en **`http://localhost:8080/api`**. Por ejemplo: `http://localhost:8080/api/products`.

El almacenamiento de PDF en Supabase está **desactivado por defecto**. Para activarlo, define las variables de entorno `SUPABASE_ENABLED=true`, `SUPABASE_URL` y `SUPABASE_SECRET_KEY`.

## Testing

Con Docker Desktop abierto (algunos tests usan Testcontainers):

```bash
./mvnw test
```

El informe de cobertura de **JaCoCo** se genera en `target/site/jacoco/index.html`. La cobertura mínima exigida es del **70 %**.

## Documentación

Toda la documentación técnica está en la [wiki del backend](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki):

- [Get started](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Get-started): dependencias, tecnologías y estructura.
- [Login](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Login) y [Register](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Register): autenticación.
- [Eventos](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Eventos), [Ofertas](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Ofertas) y [Contacto](https://github.com/FactoriaF5-Asturias/project-p5-digital-academy-team2-restaurant-backend/wiki/Contacto).

## Autores

Proyecto desarrollado por:

- [Andrea Vallina González](https://github.com/AndreaVaGo)
- [Gema Miguel](https://github.com/gmp395)
- [Jenny Sánchez Requejo](https://github.com/Jennydev-25)
- [Juan Isidro Menéndez](https://github.com/JuanIsidroMenendez)
- [Ruddy Cruz Campoverde](https://github.com/ruddycruzc)