package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionRecord;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("FROM TransactionRecord tr WHERE tr.id IN :ids")
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "3000")
    })
    List<TransactionRecord> lockAllById(List<Long> ids);
}