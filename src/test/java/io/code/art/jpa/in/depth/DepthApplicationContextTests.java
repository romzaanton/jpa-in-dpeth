package io.code.art.jpa.in.depth;

import com.github.javafaker.Faker;
import io.code.art.jpa.in.depth.models.ClearingRecord;
import io.code.art.jpa.in.depth.repository.ClearingRecordRepository;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@SpringBootTest(classes = DepthApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepthApplicationContextTests {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres-ispell:0.0.1").asCompatibleSubstituteFor("postgres"));
    static Faker faker = new Faker();
    @Autowired
    private ClearingRecordRepository clearingRecordRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        clearingRecordRepository.deleteAll();
    }


    @Test
    void saveClearingRecords() {
        var entities = IntStream.range(0, 100)
                .mapToObj(i -> ClearingRecord.builder()
                        .id(faker.number().numberBetween(1L, 100L))
                        .targetNumber(faker.numerify("###-####-##-########"))
                        .transAmount(faker.number().randomDouble(2, 0, Integer.MAX_VALUE))
                        .transactionDate(new Date())
                        .transCurr("810")
                        .unmapped(Map.of("TRANS_COUNTRY", faker.country().name()))
                        .commentText(faker.lorem().fixedString(8096))
                        .postingDate(new Date())
                        .attributes(Map.of("TRANS_COUNTRY", faker.country().name()))
                        .build())
                .collect(Collectors.toList());
        clearingRecordRepository.saveAll(entities);
        clearingRecordRepository.findAll()
                .forEach(entity -> assertTrue(entities.stream().anyMatch(item -> item.getId().equals(entity.getId()))));
    }
}
