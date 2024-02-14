package io.code.art.jpa.in.depth.repository.specification;

import io.code.art.jpa.in.depth.models.TransactionContentQueryParams;
import io.code.art.jpa.in.depth.models.TransactionLog;
import io.code.art.jpa.in.depth.models.TransactionLog_;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.hibernate.query.sqm.internal.SqmCriteriaNodeBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath.FUNCTION_NAME;

@AllArgsConstructor
public class TransactionLogSpecification implements Specification<TransactionLog> {
    private final TransactionContentQueryParams queryParams;

    @Override
    public Predicate toPredicate(Root<TransactionLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (criteriaBuilder instanceof SqmCriteriaNodeBuilder cb) {
            if (queryParams.getTransAmountFrom() != null) {
                var func1 = cb.function(
                        FUNCTION_NAME,
                        Boolean.class,
                        new Expression[]{
                                root.get(TransactionLog_.CONTENT),
                                cb.literal("$.transAmount"),
                                cb.literal(">"),
                                cb.literal(queryParams.getTransAmountFrom()),
                        }
                );
                predicates.add(cb.and(cb.isTrue(func1)));
            }

            if (queryParams.getTransAmountTo() != null) {
                var func1 = cb.function(
                        FUNCTION_NAME,
                        Boolean.class,
                        new Expression[]{
                                root.get(TransactionLog_.CONTENT),
                                cb.literal("$.transAmount"),
                                cb.literal("<"),
                                cb.literal(queryParams.getTransAmountFrom())
                        }
                );
                predicates.add(cb.and(cb.isTrue(func1)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }
        return criteriaBuilder.and();
    }
}
