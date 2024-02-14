package io.code.art.jpa.in.depth.repository.functions;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentsValidator;
import org.hibernate.query.sqm.produce.function.FunctionArgumentException;
import org.hibernate.query.sqm.produce.function.FunctionArgumentTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.sql.internal.SqmParameterInterpretation;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.query.sqm.tree.domain.SqmPath;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;

/**
 * This method works with numeric values and jsonpath value
 */
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
        column.accept(walker);
        sqlAppender.append(" @@ ");
        if (sqlAstArguments.get(3) instanceof QueryLiteral) {
            sqlAppender.append("'");
            sqlAppender.append(fieldLiteral.getLiteralValue());
            sqlAppender.append(operatorLiteral.getLiteralValue());
            sqlAppender.append(((QueryLiteral<Double>) sqlAstArguments.get(3)).getLiteralValue().toString());
            sqlAppender.append("'");
        } else if (sqlAstArguments.get(3) instanceof SqmParameterInterpretation) {
            sqlAppender.append(" CONCAT(");
            sqlAppender.append("'");
            sqlAppender.append(fieldLiteral.getLiteralValue());
            sqlAppender.append("'");
            sqlAppender.append(", ");
            sqlAppender.append("'");
            sqlAppender.append(operatorLiteral.getLiteralValue());
            sqlAppender.append("'");
            sqlAppender.append(", ");
            sqlAstArguments.get(3).accept(walker);
            sqlAppender.append(")::jsonpath");
        }
    }

    public static class NumericValueJsonPathArgumentsValidator implements ArgumentsValidator {
        @Override
        public void validate(List<? extends SqmTypedNode<?>> arguments, String functionName, TypeConfiguration typeConfiguration) {
            if (arguments.size() != 4) {
                throw new FunctionArgumentException(
                        String.format("%s function expect exactly 4 arguments", FUNCTION_NAME)
                );
            }
            if (!(arguments.get(0) instanceof SqmPath)) {
                throw new FunctionArgumentException(
                        String.format("%s function first arguments have to be sqm path", FUNCTION_NAME)
                );
            }
            if (arguments.subList(1, arguments.size()).stream().allMatch(arg -> arg instanceof QueryLiteral)) {
                throw new FunctionArgumentException(
                        String.format("%s function arguments from 2 to 4 have to be literals value", FUNCTION_NAME)
                );
            }
            ArgumentsValidator.super.validate(arguments, functionName, typeConfiguration);
        }

        @Override
        public void validateSqlTypes(List<? extends SqlAstNode> arguments, String functionName) {
            ArgumentsValidator.super.validateSqlTypes(arguments, functionName);
        }
    }

}
