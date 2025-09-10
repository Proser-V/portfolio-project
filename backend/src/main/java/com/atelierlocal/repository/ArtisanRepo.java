package com.atelierlocal.repository;

import com.atelierlocal.model.Artisan;
import com.atelierlocal.model.ArtisanCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JpaRepository key inherited methods include:
 * - save(), saveAll(): persist or update entities
 * - findById(), findAll(), findAllById(): retrieve entities
 * - existsById(): check for existence
 * - delete(), deleteById(), deleteAll(): remove entities
 * - count(): count total number of records
 */

@Repository
public interface ArtisanRepo extends JpaRepository<Artisan, UUID> {
    Optional<Artisan> findByEmail(String email);
    List<Artisan> findAllByCategory(ArtisanCategory artisanCategory);
}
