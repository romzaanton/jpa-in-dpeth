package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionRecord;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("FROM TransactionRecord tr WHERE tr.id IN :ids")
    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")
    })
    void lockAllById(List<Long> ids);

    @Modifying
    @Query(value = "LOCK TABLE TRANSACTION_RECORD IN EXCLUSIVE MODE", nativeQuery = true)
    void lockTable();
}