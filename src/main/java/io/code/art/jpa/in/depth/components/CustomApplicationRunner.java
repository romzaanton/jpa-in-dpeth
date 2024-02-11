package io.code.art.jpa.in.depth.components;

import io.code.art.jpa.in.depth.models.TransactionContentQueryParams;
import io.code.art.jpa.in.depth.repository.TransactionLogRepository;
import io.code.art.jpa.in.depth.repository.specification.TransactionLogSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {
    private final TransactionLogRepository transactionLogRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        transactionLogRepository.findAll(
                new TransactionLogSpecification(
                        TransactionContentQueryParams.builder()
                                .transAmountFrom(100.00)
                                .build()
                )
        );
    }
}
