# Extending JPA for working with PostgreSQL

###### 

###### Aim and goal of the article

Fine tune of  code base for JPA for working with PostgreSQL is crucial part of your application and it's further extending and modification.

Why we still need to extend our functionality while working with JPA and PostgreSQL?

JPA, aka Hibernate, is general framework, adopted to work with variety of relational DB,'s but we have noticeable batch of features that it's not available form out of box and were presented as rough cut, among them:

* JSONB and it's native function
* Full text search
* Enumerated type, Ranges and etc.

JPA extension for PostgreSQL open to developers new approaches for handling such task as:

* audit and replication,
* security check on application layer,
* caching and versioning,
* powered NoSQL features in RDBMS,
* etc.

What the aim at persistence layer design:

* fail fast, preferable at compile time or up time,
* single point of extension, that can be moderate by one and used by many,
* simple code construction, that will be debuggable and extensible

‍

###### Where is points to start JPA extending?

Dialect - translator from "common" SQL to specific RDBMS  SQL provided by hibernate properties `hibernate.dialect`​.

Out of the box you have support for most popular RDBMS engines such as: Oracle, MySQL, PostgreSQL. For example:

* common SQL Cast operators to DB specific implemetation,
* lock semantic,
* CTE support,
* etc.

Even as, you decide to go with dialect extending, we will use such code element as:

* ​`FunctionContributions`​ - help to register custom HQL function implementation. It's look like:

  ​![carbon (1)](assets/carbon%201-20240610205925-sshbx58.png)​
* ​`TypeContributions`​ - solution point for custom types to inject in your application. In general, you will see the snippet like this:

  ​![carbon (2)](assets/carbon%202-20240610210301-3rm4x9e.png)​

JdbcType and JavaType - JPA interfaces that helps to describe custom type serialization and deserialization.

in edge cases, we can use such features as:

* statement interceptors - case when you receive DB specific SQL string before flush and should return SQL string with or without any modification provided with property `hibernate.session_factory.statement_inspector`​
* Query rewriter - available in latest Spring data JPA package
* AttributeConverter - for straightforward cases, such as string to boolean converter and etc.

‍

*** Text to below shouldn't be included in the article

Hibernate Event listeners - help to provide a customized behaviour for most of event at persistence layer

[How to implement hibernate event listeners](https://vladmihalcea.com/dto-projection-jpa-query "How to implement hibernate event listeners")

[Integrator providing](https://vladmihalcea.com/dto-projection-jpa-query/)

Hibernate Interceptors - single instance of "Interceptor" interface provided by hibernate property, that can help to adjust logic before core hibernate events, such as: save, update, delete and etc.

Hibernate Statement interceptor - single instance StatementInspector provided `hibernate.session_factory.statement_inspector`​

[Statement inspector](https://vladmihalcea.com/hibernate-statementinspector/)

‍

###### Hibernate 6 type mapping explained

At first step we have to explain how to map PostgreSQL to Java type/POJO. For this purpose we have two code units:

* ​`JdbcType`​
* ​`JavaType`​

To begin, we have to design class that extends `JavaType`​ and override a few methods:

* ​`unwrap`​ - create a value, that will pass to the prepared statement as parameter
* ​`wrap`​ - how to extract value from result set or callable statement

Further, `JdbcType`​ come to scene with binders and extractors.

Personally, during the most of design task for mapping system, it's crucial to watch in PostgreSQL documentation and how it parse from byte array or string for further value wrap into `PGObject`​.

‍

###### Mapping JSONB from the ground up

Let's assume, we need to utilize JSONB type, with absence to use external libraries or approaches from the box like `JdbcTypeCode`​ with value `SqlTypes.JSON`​.

> ​`JdbcTypeCode`​ out the box solution, but this approach has it's own downsides:
>
> describe the error when your work in one module with JSONB through `JdbcTypeCode`​ and

From PostgreSQL documentation we have JSONB representation as RFC7159 string and, same as we can get in Java code.

Let's start with `JavaType`​:

```java
import org.hibernate.type.descriptor.java.BasicJavaType;
import java.util.Map;

public class RecordAttributeJavaType implements BasicJavaType<Map> {
	...
}
```

> There is a need to clarify why `java.util.Map`​ comes as value type on Java side: for our test case we expected that JSON value keep an arbitrary data such user defined document attributes.

For this task with gonna use PostgreSQL JDBC library/drivers and Jackson project to convert Java Map to JSON string to pass it to a prepared statement:

```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.postgresql.util.PGobject;
import java.util.Map;

...
private static final TypeReference<Map<String, Serializable>> typeRef = new TypeReference<>() {};
...

@Override
@SuppressWarnings("unchecked")
public <X> X unwrap(Map value, Class<X> type, WrapperOptions options) {
	PGobject obj = new PGobject();
    obj.setType("jsonb");
    try {
		obj.setValue(objectMapper.writeValueAsString(value));
    } catch (JsonProcessingException | SQLException e) {
    	throw new RuntimeException(e);
    }
    return obj;
}
```

Now, that's question in what form we will get value from result set. For our case, best suited type is string or binary stream and getting ahead we have `getExtractor`​ method in `JdbcType`​ like this:

```java
public class RecordAttributeJdbcType implements JdbcType {
    ...
    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaType<X> javaType) {
        return new BasicExtractor<X>(javaType, this) {
            @Override
            protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
                return javaType.wrap(rs.getString(paramIndex), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                return javaType.wrap(statement.getString(index), options);
            }

            @Override
            protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                return javaType.wrap(statement.getString(name), options);
            }
        };
    }
}
```

After extracting a field from result set, value will be passed to the wrap method `JavaType`​:

```java
public class RecordAttributeJavaType implements BasicJavaType<Map> {
    @Override
    public <X> Map<String, Serializable> wrap(X value, WrapperOptions options) {
        if (value instanceof String str) {
            try {
                return objectMapper.readValue(str, typeRef);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (value == null) {
            return null;
        }
        throw new IllegalArgumentException("Invalid value type");
    }
}
```

Design task of type mapping is done. All we have to do is annotate our entity field:

```java
public class ClearingRecord {
...
    @JavaType(RecordAttributeJavaType.class)
    @JdbcType(RecordAttributeJdbcType.class)
    @Column(name = "attributes")
    private Map<String, Serializable> attributes;
...
}
```

In general, that is all we need to do for new type injecting in our code base. It was less obvious and straightforward in Hibernate 5.

‍

###### New method and function injection for JPA queries and Criteria API

PostgreSQL has a reach functionality for working with JSONB, but very few we have include out the box for Hibernate 6. That's why programmers have to take a hard look at such term as SQM.

Let's assume we have JSONB column with numeric value and we want to filter values with arithmetic operators:

```sql
select * from atm_transactions where content @@ '$.amount > 100.00'
```

For basic case, `FunctionRenderer`​ and `AbstractSqmSelfRenderingFunctionDescriptor`​ comes to the rescue, when we try to inject new function or operator:

```java

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
		// Used for Criteria API
        if (sqlAstArguments.get(3) instanceof QueryLiteral) {
            sqlAppender.append("'");
            sqlAppender.append(fieldLiteral.getLiteralValue());
            sqlAppender.append(operatorLiteral.getLiteralValue());
            sqlAppender.append(((QueryLiteral<Double>) sqlAstArguments.get(3)).getLiteralValue().toString());
            sqlAppender.append("'");
        // Used for JPA Queries methods 
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
```

In class below, we try to describe function that verify as to right side JSONB value include left side.

After function descirption was designed, let's register our function in dialect:

```java
public class PostgresDialectCustomized extends PostgreSQLDialect {
...
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
        var typeConfiguration = functionContributions.getTypeConfiguration();
        var functionRegistry = functionContributions.getFunctionRegistry();
        functionRegistry.register(
                io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath.FUNCTION_NAME,
                new NumericValueJsonPath(
                        io.code.art.jpa.in.depth.repository.functions.NumericValueJsonPath.FUNCTION_NAME,
                        new NumericValueJsonPath.NumericValueJsonPathArgumentsValidator(),
                        StandardFunctionReturnTypeResolvers.invariant(
                                typeConfiguration.getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN)
                        ),
                        (function, argumentIndex, converter) -> converter.determineValueMapping(function)
                )
        );
    }
...
}
```

For Dialect  implementation, we will follow the easy way and defined it application properties file:

```yaml
spring:
...
  jpa:
    properties:
      hibernate:
        dialect: io.code.art.jpa.in.depth.repository.PostgresDialectCustomized
...
```

or in java configuration class you can customize bean:

```java
@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("io.code.art.jpa.in.depth");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaPropertyMap(Map.of(
                        JdbcSettings.DIALECT, PostgresDialectCustomized.class.getTypeName()
                )
        );

        return factoryBean;
    }
```

As the way to illustrate, let's use new function in Criteria API specification:

```java
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
```

In JPA query, for working with JSON path we have to implement a negligible stunt:

```java
/// Code for JPA repoistory
public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID>, JpaSpecificationExecutor<TransactionLog> {
    @Query("FROM TransactionLog tl WHERE jsonpath_numeric_value_query(tl.content, '$.transAmount', '>', ?#{#amount.toString()})")
    List<TransactionLog> lookForTransactionContentWhereAmountGreater(Double amount);
}

...
// Code for function descriptor

public void render(SqlAppender sqlAppender, List<? extends SqlAstNode> sqlAstArguments, ReturnableType<?> returnType, SqlAstTranslator<?> walker) {
        Expression column = (Expression) sqlAstArguments.get(0);
        QueryLiteral<String> fieldLiteral = (QueryLiteral<String>) sqlAstArguments.get(1);
        QueryLiteral<String> operatorLiteral = (QueryLiteral<String>) sqlAstArguments.get(2);
        column.accept(walker);
        sqlAppender.append(" @@ ");
        ....
		if (sqlAstArguments.get(3) instanceof SqmParameterInterpretation) {
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
```

That is attribute to the fact that we can use `QueryLiteral`​ in JPQL for parameters pass and, in most cases, argument of class `SqmParameterInterpretation`​ receives in `List<? extends SqlAstNode> sqlAstArguments`​ the list.
