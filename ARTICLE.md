# Extending JPA for working with PostgreSQL

###### 

###### Aim and goal of the article

Fine tune of  code base for JPA for working with PostgreSQL is crucial part of your application and it's further extending and modification.

Why we still need to extend our functionality while working with JPA and PostgreSQL?

JPA, aka Hibernate, is general framework, adopted to work with variety of relational DB,'s but we have noticeable batch of features that it's not available form the box and were presented as rough cut, among them:

* JSONB and it's native function
* Full text search
* Enumerated type, Ranges and etc.

JPA extension open to developers new approaches for handling such task as:

* audit and replication,
* security check on application layer,
* caching and versioning,
* etc.

‍

Where is points to start JPA extending?

Dialect - translator from "common" SQL to specific RDBMS  SQL provided by hibernate properties `hibernate.dialect`​

Even as, you decide to go with dialect extending, we will use such code element as:

* ​`FunctionContributions`​
* ​`TypeContributions`​

JdbcType and JavaType - code units that helps to describe type

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

Personally, during the most of design task for mapping system, it's crucial to track to screens: PostgreSQL documentation and how it parse from byte array or string for further value wrap into `PGobject`​.

‍

###### Mapping JSONB from the ground

Let's assume, we need to utilize JSONB type, with absence to use external libraries or approaches from the box like `JdbcTypeCode`​ with value `SqlTypes.JSON`​.

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