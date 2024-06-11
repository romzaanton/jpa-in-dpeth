package io.code.art.jpa.in.depth;

import com.github.javafaker.Faker;
import io.code.art.jpa.in.depth.models.ClearingRecord;
import io.code.art.jpa.in.depth.repository.ClearingRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.LongStream;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TSQueryTest {
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1-bullseye")
            .withReuse(true)
            .withAccessToHost(true)
            .withUsername("postgres")
            .withPassword("password")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("dictionaries/ru_ru.affix"),
                    "/usr/share/postgresql/16/tsearch_data/ru_ru.affix"
            )
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("dictionaries/ru_ru.dict"),
                    "/usr/share/postgresql/16/tsearch_data/ru_ru.dict"
            )
            .withInitScript("dictionaries/init.sql")
            .withDatabaseName("jpa");
    public static Faker faker = new Faker();

    static {
        postgreSQLContainer.addExposedPort(5433);
        postgreSQLContainer.start();
    }

    @Autowired
    public ClearingRecordRepository clearingRecordRepository;
    @Autowired
    public JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
    }

    @Test
    @Order(1)
    public void saveClearingRecordFindAmountGreaterThen_IfFounded_ThenSuccess() {
        jdbcTemplate.execute(String.format(String.format("""
                        CREATE TABLE IF NOT EXISTS CLEARING_RECORD_Y%d_M%d_D%d PARTITION OF CLEARING_RECORD
                                FOR VALUES FROM ('%s') TO ('%s');
                        """, LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth(),
                DF.format(new Date()),
                DF.format(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant())))));
        LongStream.rangeClosed(0, 10).forEach(i -> {
            var clearingRecord = ClearingRecord.builder()
                    .id(i)
                    .postingDate(new Date())
                    .transactionDate(new Date())
                    .targetNumber(faker.bothify("####-####-####-##-##"))
                    .transAmount(faker.number().randomDouble(2, 0, 1_000_000))
                    .commentText("Что-то важное и не очень")
                    .build();
            clearingRecordRepository.save(clearingRecord);
        });
    }
}
