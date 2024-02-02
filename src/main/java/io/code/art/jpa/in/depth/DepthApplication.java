package io.code.art.jpa.in.depth;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import io.code.art.jpa.in.depth.repository.ClearingRecordRepository;
import io.code.art.jpa.in.depth.types.RecordAttributeJavaType;
import io.code.art.jpa.in.depth.types.RecordAttributeJdbcType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.internal.BasicTypeImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@SpringBootApplication
@RequiredArgsConstructor
public class DepthApplication {
    private final ClearingRecordRepository clearingRecordRepository;
    private final ApplicationContext applicationContext;
    private final EntityManager entityManager;
    private final EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        SpringApplication.run(DepthApplication.class, args);
    }

    @SuppressWarnings("rawtypes")
    private void customCall() {
        var bt = new BasicTypeImpl<Map>(new RecordAttributeJavaType(), new RecordAttributeJdbcType());
        var query = entityManager
                .createQuery("SELECT dr FROM ClearingRecord dr WHERE jsonContains(dr.attributes, :attribute)", ClearingRecord.class)
                .setParameter("attribute", new TypedParameterValue<>(bt, Map.of("key-1", 2)));

        query.getResultList().forEach(value -> log.info("{}", value));
    }
}
