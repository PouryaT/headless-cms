package com.pouryat.headless_cms.entity;

import com.pouryat.headless_cms.model.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private MediaType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}