package com.anoop.examples.services.message;

import com.anoop.examples.enums.Severity;
import com.anoop.examples.model.Alert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = DeviceMessagingService.class)
@TestPropertySource(properties = { "ioto.login.userId=aUser" })
public class DeviceMessagingServiceTest {

    @MockBean
    private IotoGateway gateway;
    @Autowired
    private DeviceMessagingService messagingService;

    @Test
    public void testSendHeartBeat() throws Exception {
        final String userId = "aUser";
        when(gateway.isConnected()).thenReturn(false);
        doNothing().when(gateway).connect();
        doNothing().when(gateway).sendHeartBeat(userId);

        messagingService.sendHeartBeat();

        verify(gateway, times(1)).sendHeartBeat(userId);
    }

    @Test
    public void testSendAlert() throws Exception {
        final String userId = "aUser";
        Alert alert = new Alert();
        alert.setDescription("test");
        alert.setSeverity(Severity.MAJOR);
        doNothing().when(gateway).sendAlert(userId, alert);

        messagingService.sendAlert(userId, alert);

        verify(gateway, times(1)).sendAlert(userId, alert);
    }
}
