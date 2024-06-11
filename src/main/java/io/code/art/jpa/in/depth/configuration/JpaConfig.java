package io.code.art.jpa.in.depth.configuration;

import io.code.art.jpa.in.depth.configuration.events.CustomIntegratorProvider;
import io.code.art.jpa.in.depth.configuration.events.EventListenersIntegrators;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SessionEventSettings;
import org.hibernate.jpa.boot.spi.JpaSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.type.Type;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "io.code.art.jpa.in.depth.repository"
)
@RequiredArgsConstructor
@EnableTransactionManagement
public class JpaConfig implements InitializingBean {
    private final DataSource dataSource;
    private final EventListenersIntegrators eventListenersIntegrators;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("io.code.art.jpa.in.depth");
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaPropertyMap(Map.of(
                        JdbcSettings.STATEMENT_INSPECTOR, (StatementInspector) (sql) -> {
                            log.debug("STATEMENT_INSPECTOR: {}", sql);
                            return sql;
                        },
                        JpaSettings.INTEGRATOR_PROVIDER, new CustomIntegratorProvider(),
                        SessionEventSettings.INTERCEPTOR, new Interceptor() {
                            @Override
                            public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
                                return Interceptor.super.onLoad(entity, id, state, propertyNames, types);
                            }
                        },
                        JdbcSettings.DIALECT, PostgresDialectCustomized.class.getTypeName()
                )
        );

        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

}


