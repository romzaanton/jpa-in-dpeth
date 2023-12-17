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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@SpringBootApplication
@RequiredArgsConstructor
public class DepthApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(DepthApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Application runner in action");

//        var sf = sessionFactory.unwrap(SessionFactoryImpl.class).getQueryEngine();
//        var sqmTranslator = sf.getSqmTranslatorFactory();
//        var sqlSelection = new SqmSelectStatement<ClearingRecord>(SqmQuerySource.OTHER, sf.getCriteriaBuilder());
//        var sqlSelection_ = new SqmSelectClause(false, sf.getCriteriaBuilder());
//        sqlSelection.getQuerySpec().setSelectClause(sqlSelection_);
//        var root = sqlSelection.from(ClearingRecord.class);
//        var from = new SqmFromClause(1);
//        from.addRoot(root);
//        sqlSelection.select(root);
//
//        var sqmConverter = sqmTranslator.createSelectTranslator(
//                sqlSelection,
//                new SimpleQueryOptions(new LockOptions(), true),
//                DomainParameterXref.empty(),
//                null,
//                new LoadQueryInfluencers(sf.getCriteriaBuilder().getSessionFactory()),
//                sessionFactory.unwrap(SqlAstCreationContext.class),
//                false
//        );
//
//        var ast = sqmConverter.translate().getSqlAst();
//
//
//
//        final JdbcServices jdbcServices = sessionFactory.unwrap(SessionFactoryImpl.class).getJdbcServices();
//
//        final JdbcEnvironment jdbcEnvironment = jdbcServices.getJdbcEnvironment();
//        final SqlAstTranslatorFactory sqlAstTranslatorFactory = jdbcEnvironment.getSqlAstTranslatorFactory();
//        final var translator = sqlAstTranslatorFactory.buildSelectTranslator(sessionFactory.unwrap(SessionFactoryImplementor.class), ast);
//        final var jdbcSelect = translator.translate(new JdbcParameterBindingsImpl(0), new SimpleQueryOptions(LockOptions.NONE, true));


//        clearingRecordRepository.findAll(new ClearingRecordSearchSpecification());

    }


}
