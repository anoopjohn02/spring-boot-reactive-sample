package com.anoop.examples.services.alerts;

import com.anoop.examples.services.message.DeviceMessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlertEventListener {

    @Autowired
    private DeviceMessagingService messagingService;
    /**
     * Method to handle {@link AlertCreatedEvent}
     *
     * @param event alert created event
     */
    @Async
    @EventListener
    public void handleAlertCreatedEvent(AlertCreatedEvent event) {
        try {
            messagingService.sendAlert(event.getUser().getUserName(), event.getAlert());
        } catch (Exception e) {
            log.error("Error inside handleAlertCreatedEvent", e);
        }
    }
}
