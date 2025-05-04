package com.library.service;

import com.library.model.Book;
import com.library.model.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Page<Book> getAllBooks(Pageable pageable);

    Optional<Book> getBookById(Long id);

    Book createBook(Book book);

    Book updateBook(Long id, Book bookDetails);

    void deleteBook(Long id);

    Page<Book> searchBooks(String keyword, Pageable pageable);

    Page<Book> searchBooksByAuthor(String authorName, Pageable pageable);

    Page<Book> searchBooksByName(String bookName, Pageable pageable);

    Page<Book> getBooksByTopic(Long topicId, Pageable pageable);

    Page<Book> getAvailableBooks(Pageable pageable);

    List<Book> getMostRentedBooks(int limit);

    List<Book> getMostAvailableBooks(int limit);

    BookCopy createBookCopy(Book book);

    void updateBookCopyStatus(Long copyId, String status);

    Optional<BookCopy> getAvailableBookCopy(Book book);

    long getAvailableCopyCount(Book book);

    long getTotalCopyCount(Book book);

    void addAuthorToBook(Long bookId, Long authorId);

    void removeAuthorFromBook(Long bookId, Long authorId);

    List<BookCopy> searchAvailableBookCopies(Long bookId);
}