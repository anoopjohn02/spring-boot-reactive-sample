package com.anoop.examples.services.measurements;

import com.anoop.examples.enums.CustomAlertType;
import com.anoop.examples.enums.Severity;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.services.alerts.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MeasurementEventListener {

    @Autowired
    private AlertService alertService;

    /**
     * Method to handle {@link MeasurementCrossedLimitEvent}.
     * <p>Create an {@link Alert} and send to {@link
     * AlertService#create(Alert, IotoUser)}</p>
     *
     * @param event the {@link MeasurementCrossedLimitEvent}
     */
    @Async
    @EventListener
    public void handleVoltageCrossedLimitEvent(MeasurementCrossedLimitEvent event) {
        sendAlert(event.getUser(), event.getAlertType());
    }

    private void sendAlert(IotoUser user, CustomAlertType alertType){
        Alert alert = new Alert();
        alert.setDeviceId(user.getUserName());
        alert.setKey(alertType.getKey());
        alert.setDescription(alertType.getDescription());
        alert.setSeverity(Severity.MAJOR);
        alert.setType(alertType.name());
        alert.setDateTime(new Date());
        alertService.create(alert, user);
    }
}
