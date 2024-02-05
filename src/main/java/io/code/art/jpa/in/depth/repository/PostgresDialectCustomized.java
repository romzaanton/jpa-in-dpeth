package io.code.art.jpa.in.depth.repository;

import io.code.art.jpa.in.depth.types.RecordAttributeJavaType;
import io.code.art.jpa.in.depth.types.RecordAttributeJdbcType;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.spi.BasicTypeRegistration;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.internal.BasicTypeImpl;

import java.util.List;

import static io.code.art.jpa.in.depth.repository.JsonSqmPathFunctionDescriptor.FUNCTION_NAME;

public class PostgresDialectCustomized extends PostgreSQLDialect {
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        var typeConfiguration = functionContributions.getTypeConfiguration();
        functionContributions.getFunctionRegistry().registerPattern("json_contains_pattern", "(?1::jsonb @> ?2::jsonb)",
                basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
        functionContributions.getFunctionRegistry().register(
                FUNCTION_NAME,
                new JsonSqmPathFunctionDescriptor(
                        FUNCTION_NAME,
                        new JsonSqmPathFunctionDescriptor.JsonSqmPathArgumentsValidator(),
                        StandardFunctionReturnTypeResolvers.invariant(
                                typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)
                        ),
                        new JsonSqmPathFunctionDescriptor.JsonSqmPathFunctionArgumentTypeResolver()
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
