package com.atelierlocal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Message;
import com.atelierlocal.model.User;

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
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
        UUID senderId, UUID receiverId, UUID senderId2, UUID receiverId2
    );

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId OR m.receiver.id = :userId) " +
       "AND m.createdAt = (SELECT MAX(m2.createdAt) FROM Message m2 WHERE " +
       "(m2.sender.id = m.sender.id AND m2.receiver.id = m.receiver.id) OR " +
       "(m2.sender.id = m.receiver.id AND m2.receiver.id = m.sender.id))")
    List<Message> findAllByUserId(UUID userId);

    List<Message> findByReceiverAndIsReadFalse(User receiver);

    List<Message> findAllBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
}
