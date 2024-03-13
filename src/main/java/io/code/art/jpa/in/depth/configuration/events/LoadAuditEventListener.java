package io.code.art.jpa.in.depth.configuration.events;

import io.code.art.jpa.in.depth.models.ClearingRecord;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoadAuditEventListener implements PreLoadEventListener {
    @Override
    public void onPreLoad(PreLoadEvent event) {
        var ent = event.getEntity();
        if (ent instanceof ClearingRecord clearingRecord) {
            clearingRecord.setId(1002L);
        }
        log.debug("Load event for Entity: {}", event.getId());
    }
}
