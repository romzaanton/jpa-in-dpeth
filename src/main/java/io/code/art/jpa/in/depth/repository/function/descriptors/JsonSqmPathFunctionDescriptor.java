package io.code.art.jpa.in.depth.repository.function.descriptors;

import org.hibernate.metamodel.mapping.BasicValuedMapping;
import org.hibernate.metamodel.mapping.MappingModelExpressible;
import org.hibernate.query.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.ArgumentsValidator;
import org.hibernate.query.sqm.produce.function.FunctionArgumentTypeResolver;
import org.hibernate.query.sqm.produce.function.FunctionReturnTypeResolver;
import org.hibernate.query.sqm.sql.SqmToSqlAstConverter;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.query.sqm.tree.expression.SqmFunction;
import org.hibernate.sql.ast.SqlAstNodeRenderingMode;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.spi.TypeConfiguration;

import java.util.List;
import java.util.function.Supplier;

public class JsonSqmPathFunctionDescriptor extends AbstractSqmSelfRenderingFunctionDescriptor {
    public static String FUNCTION_KEY = "compare_jsonb_right";
    public JsonSqmPathFunctionDescriptor(String name,
                                         ArgumentsValidator argumentsValidator,
                                         FunctionReturnTypeResolver returnTypeResolver,
                                         FunctionArgumentTypeResolver argumentTypeResolver) {
        super(name, argumentsValidator, returnTypeResolver, argumentTypeResolver);
    }


    @Override
    public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, ReturnableType<?> returnType, SqlAstTranslator<?> walker) {
        walker.render(sqlAstArguments.get(0), SqlAstNodeRenderingMode.DEFAULT);
        sqlAppender.append(" @> ");
        walker.render(sqlAstArguments.get(1), SqlAstNodeRenderingMode.DEFAULT);
    }

    public static class JsonSqmPathArgumentsValidator implements ArgumentsValidator {
        @Override
        public void validate(List<? extends SqmTypedNode<?>> arguments, String functionName, TypeConfiguration typeConfiguration) {
            ArgumentsValidator.super.validate(arguments, functionName, typeConfiguration);
        }

        @Override
        public void validateSqlTypes(List<? extends SqlAstNode> arguments, String functionName) {
            ArgumentsValidator.super.validateSqlTypes(arguments, functionName);
        }
    }

    public static class JsonSqmPathFunctionReturnTypeResolver implements FunctionReturnTypeResolver {

        @Override
        public ReturnableType<?> resolveFunctionReturnType(ReturnableType<?> impliedType, Supplier<MappingModelExpressible<?>> inferredTypeSupplier, List<? extends SqmTypedNode<?>> arguments, TypeConfiguration typeConfiguration) {
            return typeConfiguration.getBasicTypeRegistry().resolve(BooleanJavaType.class, SqlTypes.BOOLEAN);
        }

        @Override
        public BasicValuedMapping resolveFunctionReturnType(Supplier<BasicValuedMapping> impliedTypeAccess, List<? extends SqlAstNode> arguments) {
            return impliedTypeAccess.get();
        }
    }

    public static class JsonSqmPathFunctionArgumentTypeResolver implements FunctionArgumentTypeResolver {
        @Override
        public MappingModelExpressible<?> resolveFunctionArgumentType(SqmFunction<?> function, int argumentIndex, SqmToSqlAstConverter converter) {
            return converter.determineValueMapping(function);
        }
    }
}
