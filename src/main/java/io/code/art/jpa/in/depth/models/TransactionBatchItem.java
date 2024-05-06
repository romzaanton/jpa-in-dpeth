package io.code.art.jpa.in.depth.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "TRANSACTION_BATCH_ITEM")
public class TransactionBatchItem {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID uuid;
    private String sender;
    private String receiver;
    private Double amount;

    @Column(name = "batch_uuid", insertable = false, updatable = false)
    private UUID batchUUID;

    @JoinColumn(name = "batch_uuid", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @ToString.Exclude
    private TransactionBatch batch;
}
