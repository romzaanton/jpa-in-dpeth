package io.code.art.jpa.in.depth.configuration.events;

import lombok.NoArgsConstructor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;

import java.util.List;

@NoArgsConstructor
public class CustomIntegratorProvider implements IntegratorProvider {
    @Override
    public List<Integrator> getIntegrators() {
        return List.of(
                new EventListenersIntegrators()
        );
    }
}
