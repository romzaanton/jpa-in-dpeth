package io.code.art.jpa.in.depth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionContent implements Serializable {
    private Date transactionDate;
    private Date postingDate;
    private String targetNumber;
    private String transCurr;
    private Double transAmount;
    private String commentText;
}
