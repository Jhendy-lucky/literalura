package com.alura.literalura.service;

import com.alura.literalura.model.Author;
import com.alura.literalura.model.Book;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void saveBook(Book book) {
        // Gestionar relaciones de los autores
        List<Author> authors = book.getAuthors().stream()
                .map(author -> {
                    if (author.getId() != null && authorRepository.existsById(author.getId())) {
                        return authorRepository.findById(author.getId()).orElse(author);
                    }
                    return author;
                })
                .toList();
        book.setAuthors(authors);

        // Guardar el libro
        bookRepository.save(book);
    }


    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Author> getAuthorsAliveInYear(int year) {
        return authorRepository.findByBirthYearLessThanEqualAndDeathYearGreaterThanOrDeathYearIsNull(year, year);
    }

    public long getBooksCountByLanguage(String language) {
        return bookRepository.countBooksByLanguage(language);
    }
}
