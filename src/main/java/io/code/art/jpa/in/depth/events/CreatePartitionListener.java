package io.code.art.jpa.in.depth.events;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePartitionListener implements ApplicationListener<ApplicationStartedEvent> {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        createPartition();
    }

    private void createPartition() {
        var date = LocalDate.now();
        jdbcTemplate.getJdbcOperations().execute(
                String.format(
                        """
                                CREATE TABLE IF NOT EXISTS %s PARTITION OF CLEARING_RECORD
                                    FOR VALUES FROM ('%s') TO ('%s');
                                """,
                        ClearingRecord.partitionName(date),
                        date.atStartOfDay().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        date.plusDays(1).atStartOfDay().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
        );
    }
}
