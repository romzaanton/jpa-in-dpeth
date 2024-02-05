package io.code.art.jpa.in.depth.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.postgresql.util.PGobject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Map;

public class RecordAttributeJavaType implements BasicJavaType<Map> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Serializable>> typeRef = new TypeReference<>() {
    };

    @Override
    public Type getJavaType() {
        return new RecordAttributeReflectType();
    }

    private <X> PGobject createJSONBValue(X value) {
        PGobject obj = new PGobject();
        obj.setType("jsonb");
        try {
            obj.setValue(objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return new RecordAttributeJdbcType();
    }

    @Override
    @SuppressWarnings({"rawtypes"})
    public Class<Map> getJavaTypeClass() {
        return Map.class;
    }

    @Override
    public Map<String, Serializable> fromString(CharSequence string) {
        try {
            return objectMapper.readValue(string.toString(), typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(Map value, Class<X> type, WrapperOptions options) {

        return (X) createJSONBValue(value);
    }

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
