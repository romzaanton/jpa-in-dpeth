package io.code.art.jpa.in.depth.repository.functions;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentsValidator;
import org.hibernate.query.sqm.produce.function.FunctionArgumentTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;

import java.util.List;

public class NumericValueJsonPath extends AbstractSqmSelfRenderingFunctionDescriptor {
    public static final String FUNCTION_NAME = "jsonpath_numeric_value_query";

    public NumericValueJsonPath(String name, ArgumentsValidator argumentsValidator, FunctionReturnTypeResolver returnTypeResolver, FunctionArgumentTypeResolver argumentTypeResolver) {
        super(name, argumentsValidator, returnTypeResolver, argumentTypeResolver);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, ReturnableType<?> returnType, SqlAstTranslator<?> walker) {
        Expression column = (Expression) sqlAstArguments.get(0);
        QueryLiteral<String> fieldLiteral = (QueryLiteral<String>) sqlAstArguments.get(1);
        QueryLiteral<String> operatorLiteral = (QueryLiteral<String>) sqlAstArguments.get(2);
        QueryLiteral<Double> numericValue = (QueryLiteral<Double>) sqlAstArguments.get(3);
        column.accept(walker);
        sqlAppender.append(" @@ ");
        sqlAppender.append("'");
        sqlAppender.append(fieldLiteral.getLiteralValue());
        sqlAppender.append(operatorLiteral.getLiteralValue());
        sqlAppender.append(numericValue.getLiteralValue().toString());
        sqlAppender.append("'");
    }

}
