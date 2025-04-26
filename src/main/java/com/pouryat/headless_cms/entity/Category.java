package com.pouryat.headless_cms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts;
}

