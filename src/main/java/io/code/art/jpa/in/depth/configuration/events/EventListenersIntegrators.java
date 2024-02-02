package io.code.art.jpa.in.depth.configuration.events;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class EventListenersIntegrators implements Integrator {

    @Override
    public void integrate(Metadata metadata,
                          BootstrapContext bootstrapContext,
                          SessionFactoryImplementor sessionFactory) {
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        registry.appendListeners(EventType.PRE_LOAD, new LoadAuditEventListener());
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
