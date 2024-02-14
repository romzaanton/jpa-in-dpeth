package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID>, JpaSpecificationExecutor<TransactionLog> {
    @Query("FROM TransactionLog tl WHERE jsonpath_numeric_value_query(tl.content, '$.transAmount', '>', :amount)")
    List<TransactionLog> lookForTransactionContentWhereAmountGreater(Double amount);
}