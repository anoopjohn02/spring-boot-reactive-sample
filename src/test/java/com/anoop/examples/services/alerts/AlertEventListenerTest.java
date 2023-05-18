package com.anoop.examples.services.alerts;

import com.anoop.examples.enums.Severity;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.services.message.DeviceMessagingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AlertEventListener.class)
public class AlertEventListenerTest {

    @MockBean
    private DeviceMessagingService messagingService;
    @MockBean
    private IotoUser user;

    @Autowired
    private AlertEventListener alertEventListener;

    @Test
    public void testHandleAlertCreatedEvent() throws Exception {
        final String userId = "userId";
        when(user.getUserName()).thenReturn(userId);
        Alert alert = new Alert();
        alert.setDescription("test");
        alert.setSeverity(Severity.MAJOR);
        doNothing().when(messagingService).sendAlert(userId, alert);

        AlertCreatedEvent event = new AlertCreatedEvent(alert, user);
        alertEventListener.handleAlertCreatedEvent(event);

        verify(messagingService, times(1)).sendAlert(userId, alert);
    }
}
