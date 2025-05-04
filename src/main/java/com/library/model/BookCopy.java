package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_BOOK_COPY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {

    @Id
    @Column(name = "COPY_ID")
    private Long id;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "PJI_BOOK_BOOK_ID", nullable = false)
    private Book book;

    @OneToMany(mappedBy = "bookCopy")
    private Set<Rental> rentals = new HashSet<>();

    // Utility method to check if copy is available
    public boolean isAvailable() {
        return "available".equals(status);
    }
}