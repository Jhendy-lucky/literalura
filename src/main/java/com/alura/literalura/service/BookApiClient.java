package com.alura.literalura.service;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.model.BookApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class BookApiClient implements CommandLineRunner {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final BookService bookService;
    private final List<Book> searchedBooks;

    @Autowired
    public BookApiClient(BookService bookService, ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        this.objectMapper = objectMapper;
        this.bookService = bookService;
        this.searchedBooks = new ArrayList<>();
    }

    private String encodeQueryParam(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error al codificar el parámetro: " + param, e);
        }
    }

    public List<Book> fetchBooks(String apiUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                BookApiResponse apiResponse = objectMapper.readValue(response.body(), BookApiResponse.class);
                return apiResponse.getResults();
            } else {
                throw new RuntimeException("Error en la solicitud: Código de estado " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error analizando la respuesta JSON: " + e.getMessage(), e);
        }
    }

    public void buscarLibroPorTitulo(String titulo) {
        String apiUrl = "https://gutendex.com/books/?search=" + encodeQueryParam(titulo);
        List<Book> books = fetchBooks(apiUrl);

        if (!books.isEmpty()) {
            Book book = books.get(0); // Tomar el primer libro encontrado
            searchedBooks.add(book);
            System.out.println("Libro encontrado:");
            System.out.println("Título: " + book.getTitle());
            System.out.println("Autor(es): " + book.getAuthors().stream().map(author -> author.getName()).toList());
            System.out.println("Idiomas: " + book.getLanguages());
            System.out.println("Número de descargas: " + book.getDownloadCount());
        } else {
            System.out.println("No se encontró ningún libro con el título: " + titulo);
        }
    }

    public void listarLibros() {
        if (searchedBooks.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Listado de libros registrados:");
            searchedBooks.forEach(book -> {
                System.out.println("Título: " + book.getTitle());
                System.out.println("Autor(es): " + book.getAuthors().stream().map(author -> author.getName()).toList());
                System.out.println("Idiomas: " + book.getLanguages());
                System.out.println("Número de descargas: " + book.getDownloadCount());
                System.out.println("-------------------------");
            });
        }
    }

    @Override
    public void run(String... args) {
        mostrarMenu();
    }

    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- Menú de opciones ---");
            System.out.println("1. Consultar libros");
            System.out.println("2. Consultar por autor");
            System.out.println("3. Lista de autores");
            System.out.println("4. Listar autores vivos en un año");
            System.out.println("5. Guardar libro en la base de datos");
            System.out.println("6. Listar libros por idioma");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la línea restante

            switch (opcion) {
                case 1 -> consultarLibros();
                case 2 -> {
                    System.out.print("Ingrese el nombre del autor: ");
                    String autor = scanner.nextLine();
                    consultarPorAutor(autor);
                }
                case 3 -> {
                    List<Book> librosParaAutores = fetchBooks("https://gutendex.com/books/?page=2");
                    listarAutores(librosParaAutores);
                }
                case 4 -> {
                    System.out.print("Ingrese el año para filtrar autores vivos: ");
                    int year = scanner.nextInt();
                    listarAutoresVivos(year);
                }
                case 5 -> {
                    System.out.print("Ingrese el título del libro: ");
                    String tituloLibro = scanner.nextLine();
                    guardarLibroEnBaseDeDatos(tituloLibro);
                }
                case 6 -> listarLibrosPorIdioma();
                case 7 -> {
                    continuar = false;
                    System.out.println("Saliendo del programa...");
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
        scanner.close();
    }

    public void guardarLibroEnBaseDeDatos(String tituloLibro) {
        // Codificar el título para evitar errores en la URL
        String encodedTitulo = encodeQueryParam(tituloLibro);
        List<Book> librosEncontrados = fetchBooks("https://gutendex.com/books/?search=" + encodedTitulo);

        if (!librosEncontrados.isEmpty()) {
            Book libroParaGuardar = librosEncontrados.get(0); // Tomar el primer libro encontrado

            // Asegúrate de que el ID sea null para evitar conflictos
            libroParaGuardar.setId(null);

            // Log para inspeccionar el estado del libro antes de guardarlo
            System.out.println("Guardando libro con título: " + libroParaGuardar.getTitle());
            System.out.println("ID del libro (antes de guardar): " + libroParaGuardar.getId());
            libroParaGuardar.getAuthors().forEach(author ->
                    System.out.println("Autor: " + author.getName() + ", ID: " + author.getId() +
                            ", Año de nacimiento: " + author.getBirthYear() +
                            ", Año de fallecimiento: " + author.getDeathYear())
            );

            try {
                bookService.saveBook(libroParaGuardar); // Guardar el libro en la base de datos
                System.out.println("Libro guardado exitosamente en la base de datos.");
            } catch (Exception e) {
                System.out.println("Error al guardar el libro en la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontraron libros para guardar.");
        }
    }

    public void consultarLibros() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el título del libro que desea buscar: ");
        String titulo = scanner.nextLine();
        buscarLibroPorTitulo(titulo);
    }

    public void consultarPorAutor(String autor) {
        String apiUrl = "https://gutendex.com/books/?search=" + encodeQueryParam(autor);
        List<Book> books = fetchBooks(apiUrl);

        if (books.isEmpty()) {
            System.out.println("No se encontraron libros para el autor: " + autor);
        } else {
            System.out.println("\n--- Libros encontrados para el autor " + autor + " ---");
            books.forEach(book -> {
                System.out.println("Título: " + book.getTitle());
                System.out.println("Autor: " + book.getAuthors().get(0).getName());
                System.out.println("Idiomas: " + book.getLanguages());
                System.out.println("Descargas: " + book.getDownloadCount());
                System.out.println("------------------------------");
            });
        }
    }

    public void listarAutores(List<Book> books) {
        System.out.println("\n--- Lista de Autores ---");
        books.stream()
                .flatMap(book -> book.getAuthors().stream())
                .distinct()
                .forEach(author -> System.out.println(author.getName()));
    }

    public void listarAutoresVivos(int year) {
        List<Author> authors = bookService.getAuthorsAliveInYear(year);

        if (authors.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + year);
        } else {
            System.out.println("\n--- Autores vivos en el año " + year + " ---");
            authors.forEach(author -> {
                System.out.println(author.getName() +
                        " (n. " + (author.getBirthYear() != null ? author.getBirthYear() : "Desconocido") + ")");
            });
        }
    }

    public void listarLibrosPorIdioma() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el idioma (código ISO, e.g., 'en', 'fr', 'es'): ");
        String idioma = scanner.nextLine();

        try {
            long cantidad = bookService.getBooksCountByLanguage(idioma);
            System.out.println("Cantidad de libros en idioma '" + idioma + "': " + cantidad);
        } catch (Exception e) {
            System.out.println("Error al consultar los libros por idioma: " + e.getMessage());
        }
    }


}
