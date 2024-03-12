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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "TRANSACTION_RECORD")
public class TransactionRecord {
    public static final String PARTITION_KEY = "transactionDate";
    @Id
    @Column(name = "id")
    private Long id;

    @PartitionKey
    @Column(name = "transaction_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "posting_date")
    private Date postingDate;
    @Column(name = "target_number")
    private String targetNumber;
    @Column(name = "trans_curr")
    private String transCurr;
    @Column(columnDefinition = "numeric(20,2)", precision = 2, name = "trans_amount")
    private Double transAmount;
    @Column(name = "comment_text")
    private String commentText;
}
