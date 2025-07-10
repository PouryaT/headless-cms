package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}