package io.code.art.jpa.in.depth.types;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.internal.BasicTypeImpl;
import org.postgresql.util.PGobject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Arrays;

public class TSQueryValueFactory {
    public static final BasicTypeImpl<String> BASIC_TYPE = new BasicTypeImpl<>(new TSQueryJavaType(), new TSQueryJdbcType());


    public static TypedParameterValue<String> typedValue(String value) {
        return new TypedParameterValue<>(BASIC_TYPE, value);
    }
}
