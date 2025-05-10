package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PJI_BOOK_AUTHOR")
@IdClass(BookAuthorId.class)
public class BookAuthor {

    @Id
    @Column(name = "PJI_BOOK_BOOK_ID")
    private Long bookId;

    @Id
    @Column(name = "PJI_AUTHOR_AUTHOR_ID")
    private Long authorId;
}