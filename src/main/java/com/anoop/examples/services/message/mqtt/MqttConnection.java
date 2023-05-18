package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoMessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("mqtt")
@Component
public class MqttConnection {

    public static final String MQTT_PUBLISHER_ID = "ioto-device-";
    public static final String MESSAGE_URL = "app/message";

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${ioto.login.userId}")
    private String userName;

    @Value("${ioto.login.password}")
    private String password;

    @Autowired
    private IotoMqttCallback callback;

    @Autowired
    private MqttListener mqttListener;

    @Autowired
    private IMqttClient mqttClient;

    public void connect() throws MqttException {
        log.info("Connecting Mqtt");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setUserName(userName);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(10);
        options.setAutomaticReconnect(true);
        //options.setSocketFactory(SSLSocketFactory.getDefault());
        mqttClient.connect(options);
        mqttClient.setCallback(callback);
        log.info("Mqtt connected");
    }

    public boolean connected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    public void subscribe(String deviceId, IotoMessageHandler handler) throws Exception {
        mqttClient.subscribeWithResponse(MESSAGE_URL + "/" + deviceId, mqttListener);
        mqttListener.addToMessageHandler(deviceId, handler);
    }

    public void sendMessage(String deviceId, IotoMessage payload) throws Exception{
        String json = mapper.writeValueAsString(payload);
        String destination = MESSAGE_URL + "/" + deviceId;
        log.debug("URL {} json : {}", destination, json);
        MqttMessage mqttMessage = new MqttMessage(json.getBytes());
        mqttMessage.setQos(0);
        mqttMessage.setRetained(true);
        mqttClient.publish(destination, mqttMessage);
        log.debug("Msg published to {}", destination);
    }
}
