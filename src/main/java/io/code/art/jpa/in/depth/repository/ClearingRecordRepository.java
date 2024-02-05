package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import org.hibernate.annotations.JavaType;
import org.hibernate.query.TypedParameterValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ClearingRecordRepository extends JpaRepository<ClearingRecord, Long>, JpaSpecificationExecutor<ClearingRecord> {

    @Query(value = "SELECT cr FROM ClearingRecord cr WHERE cr.transAmount > :amount")
    List<ClearingRecord> getRecordsByAmountLessThen(@Param("amount") Double amount);

    @SuppressWarnings("rawtypes")
    @Query(value = "SELECT cr FROM ClearingRecord cr WHERE sqm_json_path(cr.unmapped, :unmapped)", queryRewriter = ClearingRecordQueryRewriter.class)
    List<ClearingRecord> getRecordsByUnmappedContains(
            @Param("unmapped") TypedParameterValue attribute
    );
}