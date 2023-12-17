package io.code.art.jpa.in.depth.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.code.art.jpa.in.depth.types.RecordAttributeJavaType;
import io.code.art.jpa.in.depth.types.RecordAttributeJdbcType;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CLEARING_RECORD")
@FilterDef(
        name = ClearingRecord.PARTITION_KEY,
        parameters = {
                @ParamDef(name = ClearingRecord.PARTITION_KEY, type = Object.class)
        }
)
@Filter(
        name = ClearingRecord.PARTITION_KEY,
        condition = "transaction_date = :transactionDate"
)
public class ClearingRecord {
    public static final String PARTITION_KEY = "transactionDate";
    @Id
    private Long id;

    @PartitionKey
    @Column(name = "transaction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date postingDate;
    private String targetNumber;
    private String transCurr;
    @Column(columnDefinition = "numeric(20,2)", precision = 2)
    private Double transAmount;
    private String commentText;

    @JavaType(RecordAttributeJavaType.class)
    @JdbcType(RecordAttributeJdbcType.class)
    private Map<String, Serializable> attributes;

    @JsonAnyGetter
    @JsonAnySetter
    @JavaType(RecordAttributeJavaType.class)
    @JdbcType(RecordAttributeJdbcType.class)
    private Map<String, Object> unmapped = new LinkedHashMap<>();

}
