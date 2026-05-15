package com.optimum.optimum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Genre extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String slug;
    private String name;

    protected Genre() {
    }

    public Genre(String slug, String name) {
        this.slug = slug;
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }
}
