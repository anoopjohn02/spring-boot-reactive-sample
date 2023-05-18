package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.enums.MessageType;
import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttMessage;
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

@SpringBootTest(classes = MqttListener.class)
public class MqttListenerTest {

    @MockBean
    private IotoMessageHandler handler;

    @Autowired
    private MqttListener mqttListener;

    @Test
    void testMessageArrived() throws Exception {
        final String deviceId = "deviceId";
        mqttListener.addToMessageHandler(deviceId, handler);
        doNothing().when(handler).onMessageReceived(any());

        String destination = MqttConnection.MESSAGE_URL + "/" + deviceId;
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload().getBytes());
        mqttListener.messageArrived(destination, mqttMessage);

        ArgumentCaptor<IotoMessage> captor = ArgumentCaptor.forClass(IotoMessage.class);
        verify(handler, times(1)).onMessageReceived(captor.capture());
        IotoMessage message = captor.getValue();
        assertNotNull(message);
        assertEquals(deviceId, message.getDeviceId());
        assertEquals(MessageType.OPERATION, message.getMessageType());
    }

    private String payload(){
        return "{\"messageType\": \"OPERATION\",\"deviceId\":\"deviceId\"}";
    }
}
