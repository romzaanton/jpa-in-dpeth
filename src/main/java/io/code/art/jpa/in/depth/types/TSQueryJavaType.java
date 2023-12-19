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

public class TSQueryJavaType implements BasicJavaType<String> {
    private static final Class<?>[] UNWRAP_CLASSES = {PGobject.class};

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {
        return new TSVectorJdbcType();
    }

    @Override
    public Type getJavaType() {
        return String.class;
    }

    @Override
    public String fromString(CharSequence string) {
        return string.toString();
    }

    private PGobject createValue(String value) throws SQLException {
        PGobject obj = new PGobject();
        obj.setType("tsquery");
        obj.setValue(value);
        return obj;
    }

    @Override
    public <X> X unwrap(String value, Class<X> type, WrapperOptions options) {
        if (Arrays.stream(UNWRAP_CLASSES).noneMatch(clazz -> clazz.equals(type))) {
            throw new IllegalArgumentException("Not a class from prohibited list: " + type.getName());
        }
        try {
            return value == null ? null : type.cast(createValue(value));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <X> String wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof String str) {
            return str;
        }
        throw new IllegalArgumentException("Unknown type of the value " + value + ", of class " + value.getClass().getName());
    }
}
