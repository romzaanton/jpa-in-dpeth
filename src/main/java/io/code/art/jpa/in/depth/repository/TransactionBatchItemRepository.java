package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionBatchItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionBatchItemRepository extends JpaRepository<TransactionBatchItem, UUID> {
}