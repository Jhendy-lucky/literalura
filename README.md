# Literalura

Literalura para el challenge 1 de la formación Java y Spring Framework de Alura Latam

## Funcionalidades Principales

1. **Búsqueda de Libros**: Consulta de títulos y detalles de libros desde una API pública.
2. **Almacenamiento de Datos**: Inserción y consulta de información en una base de datos.
3. **Interacción con Usuarios**: Opción de realizar diversas acciones, como:
   - Buscar libros por título o autor.
   - Explorar los libros más populares.
   - Ver detalles de libros individuales.
   - Filtrar libros por categorías o géneros.
   - Agregar libros a una lista de favoritos.
4. **Filtrado y Visualización**: Capacidad de filtrar y ordenar los resultados según las preferencias del usuario.

## Pasos para Desarrollar el Proyecto 🛠️

### 1. Configuración del Ambiente Java
- Instalar **Java 11** o superior.
- Configurar un IDE como IntelliJ IDEA, Eclipse o VS Code con soporte para Java.

### 2. Creación del Proyecto
- Inicializa un proyecto Java y configura las dependencias necesarias (por ejemplo, para manejo de JSON y conexión con base de datos).

### 3. Consumo de la API
- Realiza solicitudes HTTP a una API de libros específica.
- Procesa las respuestas JSON y conviértelas en objetos utilizables en Java.

### 4. Análisis de la Respuesta JSON
- Descompón los datos JSON para extraer información relevante como:
  - Título del libro.
  - Autor.
  - Categoría.
  - Descripción.
- Usa bibliotecas como `org.json` o Jackson para manipular el JSON.

### 5. Inserción y Consulta en la Base de Datos
- Configura una base de datos para almacenar la información de los libros.
- Crea tablas con campos como título, autor, categoría y más.
- Implementa consultas SQL para buscar y filtrar información según las necesidades del usuario.

### 6. Exhibición de Resultados
- Presenta los resultados en un formato amigable para el usuario, interactuando desde la consola.
- Usa menús para que el usuario seleccione opciones.

## Requisitos del Entorno

- **Java 11** o superior.
- Herramienta de construcción como **Maven** o **Gradle**.
- Una base de datos SQL (puede ser SQLite, MySQL, o cualquier otra).
- Conexión a internet para acceder a la API de libros.

## Cómo Ejecutar el Proyecto

1. Clona este repositorio en tu máquina local.
2. Configura las dependencias necesarias para manejar solicitudes HTTP, JSON, y la conexión con la base de datos.
3. Compila el proyecto:
   ```bash
   javac LiterAlura.java
