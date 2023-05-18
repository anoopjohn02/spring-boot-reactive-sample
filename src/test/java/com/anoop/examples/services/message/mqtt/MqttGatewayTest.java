package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.enums.MessageType;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoMessageHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MqttGateway.class)
public class MqttGatewayTest {

    @MockBean
    private MqttConnection connection;
    @MockBean
    private IotoMessageHandler handler;

    @Autowired
    private MqttGateway gateway;

    @Test
    void testConnect() throws Exception {
        doNothing().when(connection).connect();

        gateway.connect();

        verify(connection, times(1)).connect();
    }

    @Test
    void testSubscribe() throws Exception {
        final String deviceId = "deviceId";
        doNothing().when(connection).subscribe(deviceId, handler);

        gateway.subscribe(deviceId, handler);

        verify(connection, times(1)).subscribe(deviceId, handler);
    }

    @Test
    void testSendHeartBeat() throws Exception {
        final String deviceId = "deviceId";
        doNothing().when(connection).sendMessage(any(), any());
        when(connection.connected()).thenReturn(true);

        gateway.sendHeartBeat(deviceId);

        ArgumentCaptor<IotoMessage> captor = ArgumentCaptor.forClass(IotoMessage.class);
        verify(connection, times(1)).sendMessage(any(), captor.capture());
        IotoMessage message = captor.getValue();
        assertNotNull(message);
        assertEquals(deviceId, message.getDeviceId());
        assertEquals(MessageType.HEART_BEAT, message.getMessageType());
    }

    @Test
    void testSendAlert() throws Exception {
        final String deviceId = "deviceId";
        doNothing().when(connection).sendMessage(any(), any());
        when(connection.connected()).thenReturn(true);

        Alert alert = new Alert();
        alert.setDeviceId(deviceId);
        gateway.sendAlert(deviceId, alert);

        ArgumentCaptor<IotoMessage> captor = ArgumentCaptor.forClass(IotoMessage.class);
        verify(connection, times(1)).sendMessage(any(), captor.capture());
        IotoMessage message = captor.getValue();
        assertNotNull(message);
        assertEquals(deviceId, message.getDeviceId());
        assertEquals(MessageType.ALERT, message.getMessageType());
    }
}
