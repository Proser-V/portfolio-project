package com.atelierlocal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Message;

/**
 * JpaRepository key inherited methods include:
 * - save(), saveAll(): persist or update entities
 * - findById(), findAll(), findAllById(): retrieve entities
 * - existsById(): check for existence
 * - delete(), deleteById(), deleteAll(): remove entities
 * - count(): count total number of records
 */

@Repository
public interface MessageRepo extends JpaRepository<Message, UUID> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
        UUID senderId, UUID receiverId, UUID senderId2, UUID receiverId2
    );
}
