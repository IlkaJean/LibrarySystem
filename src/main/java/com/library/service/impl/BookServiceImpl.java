package com.library.service.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.BookCopy;
import com.library.repository.AuthorRepository;
import com.library.repository.BookCopyRepository;
import com.library.repository.BookRepository;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        book.setName(bookDetails.getName());
        book.setTopic(bookDetails.getTopic());

        if (bookDetails.getAuthors() != null) {
            book.setAuthors(bookDetails.getAuthors());
        }

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        bookRepository.delete(book);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchBooks(keyword, pageable);
    }

    @Override
    public Page<Book> searchBooksByAuthor(String authorName, Pageable pageable) {
        return bookRepository.findByAuthorName(authorName, pageable);
    }

    @Override
    public Page<Book> searchBooksByName(String bookName, Pageable pageable) {
        return bookRepository.findByNameContainingIgnoreCase(bookName, pageable);
    }

    @Override
    public Page<Book> getBooksByTopic(Long topicId, Pageable pageable) {
        return bookRepository.findByTopicId(topicId, pageable);
    }

    @Override
    public Page<Book> getAvailableBooks(Pageable pageable) {
        return bookRepository.findAvailableBooks(pageable);
    }

    @Override
    public List<Book> getMostRentedBooks(int limit) {
        return bookRepository.findMostRentedBooks(PageRequest.of(0, limit));
    }

    @Override
    public List<Book> getMostAvailableBooks(int limit) {
        return bookRepository.findMostAvailableBooks(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public BookCopy createBookCopy(Book book) {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setStatus("available");
        return bookCopyRepository.save(bookCopy);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateBookCopyStatus(Long copyId, String status) {
        BookCopy bookCopy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("BookCopy", "id", copyId));

        // Verify that the status change is valid
        if ("available".equals(bookCopy.getStatus()) && "borrowed".equals(status)) {
            bookCopy.setStatus(status);
            bookCopyRepository.save(bookCopy);
        } else if ("borrowed".equals(bookCopy.getStatus()) && "available".equals(status)) {
            bookCopy.setStatus(status);
            bookCopyRepository.save(bookCopy);
        } else {
            throw new IllegalStateException("Invalid status transition from " + bookCopy.getStatus() + " to " + status);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public Optional<BookCopy> getAvailableBookCopy(Book book) {
        Optional<BookCopy> optionalCopy = book.getCopies().stream()
                .filter(BookCopy::isAvailable)
                .findFirst();

        if (optionalCopy.isPresent()) {
            // Lock the copy to prevent concurrent access
            BookCopy copy = optionalCopy.get();
            copy.setStatus("reserved");
            bookCopyRepository.save(copy);
            return Optional.of(copy);
        }

        return Optional.empty();
    }

    @Override
    public long getAvailableCopyCount(Book book) {
        return book.getAvailableCopyCount();
    }

    @Override
    public long getTotalCopyCount(Book book) {
        return book.getCopies().size();
    }

    @Override
    @Transactional
    public void addAuthorToBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));

        book.getAuthors().add(author);
        author.getBooks().add(book);

        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void removeAuthorFromBook(Long bookId, Long authorId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", authorId));

        book.getAuthors().remove(author);
        author.getBooks().remove(book);

        bookRepository.save(book);
    }

    @Override
    public List<BookCopy> searchAvailableBookCopies(Long bookId) {
        return bookCopyRepository.findByBookIdAndStatus(bookId, "available");
    }
}