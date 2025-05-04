package com.library.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PJI_TOPIC")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    @Id
    @Column(name = "TOPIC_ID")
    private Long id;

    @Column(name = "TOPIC_NAME", nullable = false)
    private String name;

    @OneToMany(mappedBy = "topic")
    private Set<Book> books = new HashSet<>();
}