package com.pouryat.headless_cms.repository;

import com.pouryat.headless_cms.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
