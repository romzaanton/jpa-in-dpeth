package io.code.art.jpa.in.depth.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomStatementInterceptor implements StatementInspector {
    @Override
    public String inspect(String sql) {
        log.info("CUSTOM_STATEMENT_INTERCEPTOR: {}", sql);
        return sql;
    }
}
