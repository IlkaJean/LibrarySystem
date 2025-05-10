package com.library.service;

import com.library.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    Author saveAuthor(Author author);
    Optional<Author> getAuthorById(Long id);
    List<Author> getAllAuthors();

    Page<Author> getAllAuthors(Pageable pageable);

    Page<Author> searchAuthors(String keyword, Pageable pageable);

    List<Author> getAuthorsByBook(Long bookId);

    List<Author> getAuthorsBySeminar(Long seminarId);

    @Transactional
    Author updateAuthor(Long id, Author authorDetails);

    void deleteAuthor(Long id);

    Optional<Author> findByEmail(String email);

    Optional<Author> findByName(String firstName, String lastName);
}
