package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :authorName, '%')) OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    Page<Book> findByAuthorName(@Param("authorName") String authorName, Pageable pageable);

    Page<Book> findByTopicId(Long topicId, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.copies c WHERE c.status = 'available' GROUP BY b HAVING COUNT(c) > 0")
    Page<Book> findAvailableBooks(Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.authors a " +
            "LEFT JOIN b.topic t " +
            "WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.copies c GROUP BY b ORDER BY COUNT(c) DESC")
    List<Book> findMostAvailableBooks(Pageable pageable);

    @Query(value = "SELECT b.* FROM PJI_BOOK b " +
            "JOIN PJI_BOOK_COPY bc ON b.BOOK_ID = bc.PJI_BOOK_BOOK_ID " +
            "JOIN PJI_RENTAL r ON bc.COPY_ID = r.PJI_BOOK_COPY_COPY_ID " +
            "GROUP BY b.BOOK_ID, b.BOOK_NAME, b.PJI_TOPIC_TOPIC_ID " +
            "ORDER BY COUNT(r.RENTAL_ID) DESC",
            nativeQuery = true)
    List<Book> findMostRentedBooks(Pageable pageable);
}