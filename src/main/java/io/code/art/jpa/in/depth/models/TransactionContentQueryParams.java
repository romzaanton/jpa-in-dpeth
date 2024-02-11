package io.code.art.jpa.in.depth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionContentQueryParams implements Serializable {
    private List<Date> transactionDate;
    private List<Date> postingDate;
    private List<String> targetNumber;
    private List<String> transCurr;
    private Double transAmountFrom;
    private Double transAmountTo;
    private String commentText;
}
