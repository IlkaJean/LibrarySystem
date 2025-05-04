package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_BOOK")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @Column(name = "BOOK_ID")
    private Long id;

    @Column(name = "BOOK_NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "PJI_TOPIC_TOPIC_ID")
    private Topic topic;

    @ManyToMany
    @JoinTable(
            name = "PJI_BOOK_AUTHOR",
            joinColumns = @JoinColumn(name = "PJI_BOOK_BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "PJI_AUTHOR_AUTHOR_ID")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<BookCopy> copies = new HashSet<>();

    // Utility method to get formatted author names
    public String getAuthorNames() {
        if (authors.isEmpty()) {
            return "Unknown";
        }

        StringBuilder authorNames = new StringBuilder();
        int count = 0;
        for (Author author : authors) {
            if (count > 0) {
                authorNames.append(", ");
            }
            authorNames.append(author.getFullName());
            count++;
        }
        return authorNames.toString();
    }

    // Utility method to get available copy count
    public long getAvailableCopyCount() {
        return copies.stream()
                .filter(copy -> "available".equals(copy.getStatus()))
                .count();
    }
}