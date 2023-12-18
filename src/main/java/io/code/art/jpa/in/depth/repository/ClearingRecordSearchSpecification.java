package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import io.code.art.jpa.in.depth.models.ClearingRecord_;
import jakarta.persistence.criteria.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.mapping.JdbcMappingContainer;
import org.hibernate.query.sqm.internal.SqmCriteriaNodeBuilder;
import org.hibernate.query.sqm.tree.expression.SqmSelfRenderingExpression;
import org.hibernate.query.sqm.tree.from.SqmRoot;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.expression.SelfRenderingExpression;
import org.hibernate.sql.ast.tree.expression.SelfRenderingSqlFragmentExpression;
import org.hibernate.sql.ast.tree.predicate.SelfRenderingPredicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ClearingRecordSearchSpecification implements Specification<ClearingRecord> {
    @Override
    public Predicate toPredicate(Root<ClearingRecord> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (criteriaBuilder instanceof SqmCriteriaNodeBuilder cb) {
            var func1 = cb.<Boolean>sql("(? ->> 'TRANS_CODE') = ?",
                    Boolean.class, root.get(ClearingRecord_.UNMAPPED), cb.value("ADV"));
            predicates.add(cb.and(cb.isTrue(func1)));
            predicates.add(cb.equal(
                    new SqmSelfRenderingExpression<Expression<?>>(
                            walker -> new SelfRenderingSqlFragmentExpression("(unmapped ->> 'key-2')"),
                            null,
                            cb
                    ),
                    new SqmSelfRenderingExpression<Expression<?>>(
                            walker -> new SelfRenderingSqlFragmentExpression("'2'"),
                            null,
                            cb
                    )
            ));
            predicates.add(
                    cb.isTrue(
                            cb.<Boolean>function("sqm_json_path", Boolean.class, new Expression[] {
                                    root.get(ClearingRecord_.UNMAPPED), cb.literal("{\"key-1\": 3}")
                            })
                    )
            );
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
