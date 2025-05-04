package com.library.controller;

import com.library.dto.BookDto;
import com.library.model.Book;
import com.library.model.BookCopy;
import com.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public Page<BookDto> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAllBooks(pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(convertToDto(createdBook), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(convertToDto(updatedBook));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<BookDto> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(keyword, pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/search/author")
    public Page<BookDto> searchBooksByAuthor(
            @RequestParam String authorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooksByAuthor(authorName, pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/search/name")
    public Page<BookDto> searchBooksByName(
            @RequestParam String bookName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooksByName(bookName, pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/topic/{topicId}")
    public Page<BookDto> getBooksByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getBooksByTopic(topicId, pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/available")
    public Page<BookDto> getAvailableBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAvailableBooks(pageable);

        return books.map(this::convertToDto);
    }

    @GetMapping("/most-rented")
    public List<BookDto> getMostRentedBooks(@RequestParam(defaultValue = "10") int limit) {
        List<Book> books = bookService.getMostRentedBooks(limit);
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/most-available")
    public List<BookDto> getMostAvailableBooks(@RequestParam(defaultValue = "10") int limit) {
        List<Book> books = bookService.getMostAvailableBooks(limit);
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{bookId}/copies")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<BookCopy> createBookCopy(@PathVariable Long bookId) {
        return bookService.getBookById(bookId)
                .map(bookService::createBookCopy)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/copies/{copyId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> updateBookCopyStatus(
            @PathVariable Long copyId,
            @RequestParam String status) {
        bookService.updateBookCopyStatus(copyId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> addAuthorToBook(
            @PathVariable Long bookId,
            @PathVariable Long authorId) {
        bookService.addAuthorToBook(bookId, authorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> removeAuthorFromBook(
            @PathVariable Long bookId,
            @PathVariable Long authorId) {
        bookService.removeAuthorFromBook(bookId, authorId);
        return ResponseEntity.ok().build();
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        BeanUtils.copyProperties(book, dto);

        if (book.getTopic() != null) {
            dto.setTopicId(book.getTopic().getId());
            dto.setTopicName(book.getTopic().getName());
        }

        if (book.getAuthors() != null) {
            Set<Long> authorIds = book.getAuthors().stream()
                    .map(author -> author.getId())
                    .collect(Collectors.toSet());
            dto.setAuthorIds(authorIds);

            Set<String> authorNames = book.getAuthors().stream()
                    .map(author -> author.getFullName())
                    .collect(Collectors.toSet());
            dto.setAuthorNames(authorNames);
        }

        dto.setTotalCopies(book.getCopies().size());
        dto.setAvailableCopies((int) book.getAvailableCopyCount());

        return dto;
    }

    private Book convertToEntity(BookDto dto) {
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        return book;
    }
}