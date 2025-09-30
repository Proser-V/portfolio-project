package com.atelierlocal.repository;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.EventCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface ArtisanCategoryRepo extends JpaRepository<ArtisanCategory, UUID> {
    @Query("SELECT c FROM ArtisanCategory c WHERE LOWER(TRIM(c.name)) = LOWER(TRIM(:name))")
    Optional<ArtisanCategory> findByNameIgnoreCase(@Param("name") String name);

    List<ArtisanCategory> findByEventCategories(EventCategory eventCategory);
}
