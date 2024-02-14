package io.code.art.jpa.in.depth;

import com.github.javafaker.Faker;
import io.code.art.jpa.in.depth.models.TransactionContent;
import io.code.art.jpa.in.depth.models.TransactionContentQueryParams;
import io.code.art.jpa.in.depth.models.TransactionLog;
import io.code.art.jpa.in.depth.repository.TransactionLogRepository;
import io.code.art.jpa.in.depth.repository.specification.TransactionLogSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class JsonbDataTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.1-bullseye")
            .withReuse(true)
            .withAccessToHost(true)
            .withUsername("postgres")
            .withPassword("password")
            .withDatabaseName("jpa");
    public static Faker faker = new Faker();

    static {
        postgreSQLContainer.addExposedPort(5433);
        postgreSQLContainer.start();
    }

    @Autowired
    public TransactionLogRepository transactionLogRepository;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
    }

    public List<TransactionLog> saveSampleItems(int count) {
        return transactionLogRepository.saveAllAndFlush(
                IntStream.range(0, count)
                        .boxed()
                        .map(i -> TransactionContent.builder()
                                .transactionDate(faker.date().between(faker.date().past(2, TimeUnit.DAYS), new Date()))
                                .postingDate(faker.date().between(faker.date().past(2, TimeUnit.DAYS), new Date()))
                                .commentText(faker.lorem().paragraph(2))
                                .targetNumber(faker.business().creditCardNumber())
                                .transAmount(faker.number().randomDouble(2, 1L, 10_000_000))
                                .transCurr(faker.country().currencyCode())
                                .build())
                        .map(content -> new TransactionLog(null, content))
                        .collect(Collectors.toList())
        );
    }

    @Test
    @Order(1)
    @DisplayName("Save transactions log")
    public void ifTransactionLogSaved_thenSuccess() {
        var entities = saveSampleItems(20);
        log.info("IF_TRANSACTION_LOG_SAVED_THEN_SUCCESS: saved count {}", entities.size());
        Assertions.assertFalse(entities.isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("Find transactions logs by amount")
    public void TransactionWithValueGreaterThenZeroLogShouldExists() {

        saveSampleItems(100);

        var entities = transactionLogRepository.findAll(new TransactionLogSpecification(
                TransactionContentQueryParams.builder()
                        .transAmountFrom(1.00)
                        .build()
        ));
        Assertions.assertFalse(entities.isEmpty());

        Assertions.assertDoesNotThrow(() -> {
            transactionLogRepository.lookForTransactionContentWhereAmountGreater(20.0);
        });

        Assertions.assertFalse(transactionLogRepository.lookForTransactionContentWhereAmountGreater(1.0).isEmpty());
    }

}
