package io.code.art.jpa.in.depth.configuration;

import io.code.art.jpa.in.depth.configuration.PostgresDialectCustomized;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

public class CustomDialectResolver implements DialectResolver {
    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        return new PostgresDialectCustomized();
    }
}
