package io.code.art.jpa.in.depth;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.jpa.boot.spi.JpaSettings;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HibernatePropertiesCustomizerConfig implements HibernatePropertiesCustomizer {
    private final PartitionAwareIntegrator partitionAwareIntegrator;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                JpaSettings.INTEGRATOR_PROVIDER,
                (IntegratorProvider) () -> List.of(partitionAwareIntegrator)
        );
    }

    @Component
    public static class InsertPartitionAwareListener implements PreInsertEventListener {
        @Override
        public boolean onPreInsert(PreInsertEvent event) {
            if (event == null) {
                return false;
            }
            if (event.getEntity() instanceof ClearingRecord cr) {

                var session = event.getSession();
                session.enableFilter(ClearingRecord.PARTITION_KEY)
                        .setParameter(
                                ClearingRecord.PARTITION_KEY,
                                cr.getTransactionDate()
                        );
            }
            return false;
        }
    }

    @Component
    public static class UpdatePartitionAwareListener implements PreUpdateEventListener {
        @Override
        public boolean onPreUpdate(PreUpdateEvent event) {
            if (event.getEntity() instanceof ClearingRecord cr) {
                event.getSession()
                        .getEnabledFilter(ClearingRecord.PARTITION_KEY)
                        .setParameter(
                                ClearingRecord.PARTITION_KEY,
                                cr.getTransactionDate()
                        );
            }
            return false;
        }
    }

    @Component
    public static class LoadPartitionAwareListener implements PreLoadEventListener {


        @Override
        public void onPreLoad(PreLoadEvent event) {
            if (event.getEntity() instanceof ClearingRecord cr) {
                var session = event.getSession();
                session.enableFilter(ClearingRecord.PARTITION_KEY)
                        .setParameter(
                                ClearingRecord.PARTITION_KEY,
                                cr.getTransactionDate()
                        );
            }
        }
    }

    @Component
    @RequiredArgsConstructor
    public static class PartitionAwareIntegrator implements Integrator {
        private final InsertPartitionAwareListener insertPartitionAwareListener;
        private final UpdatePartitionAwareListener updatePartitionAwareListener;
        private final LoadPartitionAwareListener loadPartitionAwareListener;

        @Override
        public void integrate(Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
            EventListenerRegistry eventListenerRegistry = sessionFactory.getServiceRegistry()
                    .getService(EventListenerRegistry.class);

            assert eventListenerRegistry != null;

            eventListenerRegistry.prependListeners(
                    EventType.PRE_INSERT,
                    insertPartitionAwareListener
            );

            eventListenerRegistry.prependListeners(
                    EventType.PRE_UPDATE,
                    updatePartitionAwareListener
            );

            eventListenerRegistry.prependListeners(
                    EventType.PRE_LOAD,
                    loadPartitionAwareListener
            );
        }

        @Override
        public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

        }
    }
}
