package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.repository.functions.JsonContains;
import io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath;
import io.code.art.jpa.in.depth.types.RecordAttributeJavaType;
import io.code.art.jpa.in.depth.types.RecordAttributeJdbcType;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.internal.BasicTypeImpl;

import java.util.List;


public class PostgresDialectCustomized extends PostgreSQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        var typeConfiguration = functionContributions.getTypeConfiguration();
        var functionRegistry = functionContributions.getFunctionRegistry();
        functionRegistry.registerPattern("json_contains_pattern", "(?1::jsonb @> ?2::jsonb)",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionRegistry.register(
                io.code.art.jpa.in.depth.repository.functions.JsonContains.FUNCTION_NAME,
                new JsonContains(
                        io.code.art.jpa.in.depth.repository.functions.JsonContains.FUNCTION_NAME,
                        new JsonContains.JsonSqmPathArgumentsValidator(),
                        StandardFunctionReturnTypeResolvers.invariant(
                                typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)
                        ),
                        new JsonContains.JsonSqmPathFunctionArgumentTypeResolver()
                )
        );

        functionRegistry.register(
                io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath.FUNCTION_NAME,
                new NumericValueJsonPath(
                        io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath.FUNCTION_NAME,
                        StandardArgumentsValidators.min(1),
                        StandardFunctionReturnTypeResolvers.invariant(
                                typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)
                        ),
                        (function, argumentIndex, converter) -> converter.determineValueMapping(function)
                )
        );
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions
                .contributeJavaType(new RecordAttributeJavaType());
        typeContributions.getTypeConfiguration().getJdbcTypeRegistry()
                .addDescriptor(new RecordAttributeJdbcType());
        typeContributions.getTypeConfiguration().addBasicTypeRegistrationContributions(
                List.of(
                        new BasicTypeRegistration(
                                new BasicTypeImpl<>(new RecordAttributeJavaType(), new RecordAttributeJdbcType())
                        )
                )
        );
    }
}
