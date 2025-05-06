package com.library.service.impl;

import com.library.exception.ResourceNotFoundException;
import com.library.model.Author;
import com.library.repository.AuthorRepository;
import com.library.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    @Transactional
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    @Override
    public Page<Author> searchAuthors(String keyword, Pageable pageable) {
        return authorRepository.searchAuthors(keyword, pageable);
    }

    @Override
    public List<Author> getAuthorsByBook(Long bookId) {
        return authorRepository.findByBookId(bookId);
    }

    @Override
    public List<Author> getAuthorsBySeminar(Long seminarId) {
        return authorRepository.findBySeminarId(seminarId);
    }

    @Override
    @Transactional
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));

        author.setFirstName(authorDetails.getFirstName());
        author.setLastName(authorDetails.getLastName());
        author.setStreet(authorDetails.getStreet());
        author.setCity(authorDetails.getCity());
        author.setState(authorDetails.getState());
        author.setCountry(authorDetails.getCountry());
        author.setPostalCode(authorDetails.getPostalCode());
        author.setEmail(authorDetails.getEmail());

        return authorRepository.save(author);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));

        authorRepository.delete(author);
    }

    @Override
    public Optional<Author> findByEmail(String email) {
        return authorRepository.findByEmail(email);
    }

    @Override
    public Optional<Author> findByName(String firstName, String lastName) {
        return authorRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}