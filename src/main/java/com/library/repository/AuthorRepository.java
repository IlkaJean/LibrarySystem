package com.library.repository;

import com.library.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmail(String email);

    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

    List<Author> findByBooks_Id(Long bookId); //

    List<Author> findBySeminarAttendances_Seminar_Id(Long seminarId); // assuming this is also mapped correctly

    @Query("""
        SELECT a FROM Author a
        WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Author> searchAuthors(@Param("keyword") String keyword, Pageable pageable);

}
