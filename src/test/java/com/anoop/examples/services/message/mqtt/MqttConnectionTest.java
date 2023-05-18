package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoMessageHandler;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = MqttConnection.class)
@TestPropertySource(properties = {
        "ioto.login.userId=aUser",
        "ioto.login.password=pwd"
})
public class MqttConnectionTest {

    private final String host = "http://host";
    private final String userName = "aUser";
    private final String password = "pwd";

    @MockBean
    private IotoMqttCallback callback;
    @MockBean
    private MqttListener mqttListener;
    @MockBean
    private IMqttClient mqttClient;
    @MockBean
    private IotoMessageHandler handler;

    @Autowired
    private MqttConnection mqttConnection;

    @Test
    void testConnect() throws MqttException {
        doNothing().when(mqttClient).connect(any());
        doNothing().when(mqttClient).setCallback(any());

        mqttConnection.connect();

        verify(mqttClient, times(1)).connect(any());
        verify(mqttClient, times(1)).setCallback(any());
    }

    @Test
    void testSubscribe() throws Exception {
        final String deviceId = "deviceId";
        when(mqttClient.subscribeWithResponse
                (MqttConnection.MESSAGE_URL + "/" + deviceId, mqttListener)).thenReturn(null);
        doNothing().when(mqttListener).addToMessageHandler(any(), any());

        mqttConnection.subscribe(deviceId, handler);

        verify(mqttClient, times(1)).subscribeWithResponse
                (MqttConnection.MESSAGE_URL + "/" + deviceId, mqttListener);
        verify(mqttListener, times(1)).addToMessageHandler(any(), any());
    }

    @Test
    void testSendMessage() throws Exception {
        doNothing().when(mqttClient).publish(any(), any());

        IotoMessage message = new IotoMessage();
        mqttConnection.sendMessage("deviceId", message);

        verify(mqttClient, times(1)).publish(any(), any());
    }
}
