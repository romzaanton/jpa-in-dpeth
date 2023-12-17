package io.code.art.jpa.in.depth.types;

import java.lang.reflect.Type;

public class RecordAttributeReflectType implements Type {
    private static final String NAME =  "RECORD_ATTRIBUTE_REFLECT_TYPE";
    @Override
    public String getTypeName() {
        return NAME;
    }
}
