package com.atelierlocal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Client;

/**
 * JpaRepository key inherited methods include:
 * - save(), saveAll(): persist or update entities
 * - findById(), findAll(), findAllById(): retrieve entities
 * - existsById(): check for existence
 * - delete(), deleteById(), deleteAll(): remove entities
 * - count(): count total number of records
 */

@Repository
public interface UserRepo extends JpaRepository<Client, UUID> {
    Optional<Client> findByEmail(String email);
}
