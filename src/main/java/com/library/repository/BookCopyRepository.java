package com.library.repository;

import com.library.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    List<BookCopy> findByStatus(String status);

    List<BookCopy> findByBookId(Long bookId);

    List<BookCopy> findByBookIdAndStatus(Long bookId, String status);

    @Query("SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.status = :status")
    long countByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") String status);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.status = 'available' AND bc.id NOT IN (SELECT r.bookCopy.id FROM Rental r WHERE r.status = 'Borrowed')")
    List<BookCopy> findAvailableBookCopiesByBookId(@Param("bookId") Long bookId);
}