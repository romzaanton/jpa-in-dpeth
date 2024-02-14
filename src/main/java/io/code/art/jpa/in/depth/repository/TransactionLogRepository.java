package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID>, JpaSpecificationExecutor<TransactionLog> { }