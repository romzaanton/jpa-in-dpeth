package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.TransactionRecord;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@Repository
@RequiredArgsConstructor
public class TransactionRecordConcurrentRepository {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionRecordRepository transactionRecordRepository;
    private final EntityManager entityManager;

    private <T> String tableNameFromEntity(Class<T> clazz) {
        var metadata = (MappingMetamodelImpl) entityManager.getMetamodel();
        return metadata.entityPersister(clazz).getEntityPersister().getMappedTableDetails().getTableName();
    }

    @Retryable(maxAttempts = 5)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void upsert(List<TransactionRecord> records) {
        jdbcTemplate.execute("LOCK TABLE " + tableNameFromEntity(TransactionRecord.class) + " IN EXCLUSIVE MODE");
        transactionRecordRepository.saveAllAndFlush(records);
    }

    @Retryable(maxAttempts = 5)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void upsertNoLock(List<TransactionRecord> records) {
        transactionRecordRepository.saveAllAndFlush(records);
    }
}
