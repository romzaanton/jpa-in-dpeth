package io.code.art.jpa.in.depth.repository.functions;

import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentsValidator;
import org.hibernate.query.sqm.produce.function.FunctionArgumentTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.sql.ast.SqlAstNodeRenderingMode;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;

import java.util.List;

public class TSQueryFunctionDescriptor extends AbstractSqmSelfRenderingFunctionDescriptor {
    public static String FUNCTION_NAME = "find_by_tsquery";

    public TSQueryFunctionDescriptor(String name,
                                     ArgumentsValidator argumentsValidator,
                                     FunctionReturnTypeResolver returnTypeResolver,
                                     FunctionArgumentTypeResolver argumentTypeResolver) {
        super(name, argumentsValidator, returnTypeResolver, argumentTypeResolver);
    }

    @Override
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, ReturnableType<?> returnType, SqlAstTranslator<?> walker) {
        walker.render(sqlAstArguments.get(0), SqlAstNodeRenderingMode.DEFAULT);
        sqlAppender.append(" @@ ");
        walker.render(sqlAstArguments.get(1), SqlAstNodeRenderingMode.DEFAULT);
    }
}
