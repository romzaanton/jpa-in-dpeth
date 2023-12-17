package io.code.art.jpa.in.depth.config;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import io.code.art.jpa.in.depth.repository.ClearingRecordRepository;
import io.code.art.jpa.in.depth.types.RecordAttributeJavaType;
import io.code.art.jpa.in.depth.types.RecordAttributeJdbcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.internal.BasicTypeImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppConfig implements ApplicationRunner {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ClearingRecordRepository clearingRecordRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createPartition();
        runInsert();

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

    private void runInsert() {
        clearingRecordRepository.saveAll(List.of(
                ClearingRecord.builder().id(1L).postingDate(new Date()).transactionDate(new Date()).targetNumber("KL-846-25-198")
                        .commentText("Comment to transaction")
                        .unmapped(Map.of("POSTING_STATUS", "U", "SOURCE_FEE_AMOUNT", 1_000.0, "NW_REF_DATE", new Date()))
                        .transAmount(100_000_500.0).build(),
                ClearingRecord.builder().id(2L).postingDate(new Date()).transactionDate(new Date()).targetNumber("KL-846-25-198")
                        .commentText("Comment to transaction")
                        .unmapped(Map.of("POSTING_STATUS", "C", "SOURCE_FEE_AMOUNT", 1_500.0, "NW_REF_DATE", new Date()))
                        .transAmount(1_500_500.0).build()
        ));
        var bt = new BasicTypeImpl<>(new RecordAttributeJavaType(), new RecordAttributeJdbcType());
        var records = clearingRecordRepository.getRecordsByUnmappedContains(new TypedParameterValue<>(bt, Map.of("POSTING_STATUS", "U")));
        log.info("DEPTH_APPLICATION_RECORDS: size {}", records.size());
    }
}
