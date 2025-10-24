package com.atelierlocal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atelierlocal.model.Recommendation;

@Repository
public interface RecommendationRepo extends JpaRepository<Recommendation, UUID> {}
