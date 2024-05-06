package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionBatchRepository extends JpaRepository<TransactionBatch, UUID> {
}