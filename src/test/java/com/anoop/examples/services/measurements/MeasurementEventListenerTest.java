package com.anoop.examples.services.measurements;

import com.anoop.examples.enums.CustomAlertType;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.services.alerts.AlertService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MeasurementEventListener.class)
public class MeasurementEventListenerTest {

    @MockBean
    private AlertService alertService;
    @MockBean
    private IotoUser user;

    @Autowired
    private MeasurementEventListener measurementEventListener;

    @Test
    public void testHandleVoltageCrossedLimitEvent() throws Exception {
        final String userId = "userId";
        when(user.getUserName()).thenReturn(userId);
        when(alertService.create(any(), any())).thenReturn(new Alert());

        MeasurementCrossedLimitEvent event = new MeasurementCrossedLimitEvent
                (user, CustomAlertType.ENERGY_CONSUMPTION_CROSSED);
        measurementEventListener.handleVoltageCrossedLimitEvent(event);

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertService, times(1)).create(captor.capture(), any());
        Alert alert = captor.getValue();
        assertNotNull(alert);
        assertEquals(userId, alert.getDeviceId());
    }
}
