package com.beyond.basic.b2_board.author.domain;

import jakarta.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String street;
    private String zipCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", unique = true, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT), nullable = false)
    private Author author;
}
