package com.optimum.optimum.repository;

import com.optimum.optimum.model.Genre;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Optional<Genre> findBySlug(String slug);
}
