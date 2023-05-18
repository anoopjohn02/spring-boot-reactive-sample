package com.anoop.examples.services.message.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("mqtt")
@Component
public class IotoMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable throwable) {
        log.debug("Mqtt Connection Lost...");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        log.warn("Received Unhandled message: " + s + " -> " + mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.debug("Delivery Completed...");
    }
}
