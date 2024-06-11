package io.code.art.jpa.in.depth;

import com.github.javafaker.Faker;
import io.code.art.jpa.in.depth.models.TransactionBatch;
import io.code.art.jpa.in.depth.models.TransactionBatchItem;
import io.code.art.jpa.in.depth.repository.TransactionBatchItemRepository;
import io.code.art.jpa.in.depth.repository.TransactionBatchRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static io.code.art.jpa.in.depth.models.TransactionBatch.COMPACT_TRANSACTION_BATCH_GRAPH;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EntityGraphTests {
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
    public TransactionBatchRepository transactionBatchRepository;
    @Autowired
    public TransactionBatchItemRepository transactionBatchItemRepository;
    @Autowired
    public EntityManager entityManager;

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgreSQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgreSQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.datasource.password", () -> postgreSQLContainer.getPassword());
        registry.add("spring.jpa.show-sql", () -> "true");
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> "true");
        registry.add("logging.level.org.hibernate.SQL", () -> "DEBUG");
        registry.add("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", () -> "TRACE");
        registry.add("logging.level.org.hibernate.orm.jdbc.bind", () -> "TRACE");
        registry.add("spring.jpa.properties.hibernate.enable_lazy_load_no_trans", () -> "true");
        registry.add("logging.level.org.hibernate.orm.jdbc.bind", () -> "true");
        registry.add("logging.level.org.springframework.jdbc.core.JdbcTemplate", () -> "true");
        registry.add("logging.level.org.springframework.jdbc.core.StatementCreatorUtils", () -> "true");
    }

    @Test
    @Order(1)
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void createBatch_ThenFetchWithGraph() {
        var batch = new TransactionBatch();
        batch.setTenant(faker.commerce().department());
        batch.setTransactions(new ArrayList<>());
        IntStream.rangeClosed(0, 10).boxed().forEach(i -> {
            batch.getTransactions().add(
                    new TransactionBatchItem(
                            null,
                            faker.company().name(),
                            faker.company().name(),
                            faker.number().randomDouble(2, 0L, 10_000_000L),
                            null,
                            batch
                    )
            );
        });
        var saved = transactionBatchRepository.saveAndFlush(batch);
        var bathItems = transactionBatchItemRepository.findAll();
        log.info("items: {}", bathItems);
        Assertions.assertEquals(11, bathItems.size());

        var session = entityManager.unwrap(Session.class);
        var first = session.createQuery("SELECT f FROM " + TransactionBatch.class.getSimpleName() + " f where f.uuid = :uuid", TransactionBatch.class)
                .setParameter("uuid", saved.getUuid())
                .setCacheable(false)
                .setHint("jakarta.persistence.fetchgraph", session.getEntityGraph(COMPACT_TRANSACTION_BATCH_GRAPH))
                .setHint("jakarta.persistence.loadgraph", session.getEntityGraph(COMPACT_TRANSACTION_BATCH_GRAPH))
                .getResultList().stream().findFirst().orElse(null);

        log.info("first batch: {}", first);
        if (first != null) {
            log.info("first batch items: {}", first.getTransactions().size());
        }
        Assertions.assertNotNull(first);
    }
}
