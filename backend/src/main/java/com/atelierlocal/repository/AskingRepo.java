package com.atelierlocal.repository;

import com.atelierlocal.model.ArtisanCategory;
import com.atelierlocal.model.Asking;
import com.atelierlocal.model.Client;
import com.atelierlocal.model.EventCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
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
public interface AskingRepo extends JpaRepository<Asking, UUID> {
    List<Asking> findAllByArtisanCategoryListContains(ArtisanCategory artisanCategoryList);
    List<Asking> findAllByArtisanCategoryListIn(Collection<ArtisanCategory> categories);
    List<Asking> findAllByEventCategory(EventCategory eventCategory);
    List<Asking> findAllByClient(Client client);
}
