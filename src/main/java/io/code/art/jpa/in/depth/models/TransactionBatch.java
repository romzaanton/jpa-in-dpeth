package io.code.art.jpa.in.depth.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = TransactionBatch.COMPACT_TRANSACTION_BATCH_GRAPH,
                includeAllAttributes = false,
                attributeNodes = {
                        @NamedAttributeNode("uuid"),
                        @NamedAttributeNode(value = "transactions", subgraph = "transaction_batch_item_short")
                },
                subgraphs = {
                        @NamedSubgraph(
                                name = "transaction_batch_item_short",
                                attributeNodes = {
                                        @NamedAttributeNode("uuid")
                                }
                        )
                }
        )
})
public class TransactionBatch {
    public static final String COMPACT_TRANSACTION_BATCH_GRAPH = "compact_transaction_batch_graph";

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private UUID uuid;

    private String tenant;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "batch")
    @ToString.Exclude
    @Fetch(FetchMode.SUBSELECT)
    private List<TransactionBatchItem> transactions = new ArrayList<>();
}
