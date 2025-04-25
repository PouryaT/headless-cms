package com.pouryat.headless_cms.entity;

import com.pouryat.headless_cms.model.Authorities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"authority\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Authorities authorityName; // Example: READ, CREATE, DELETE
}