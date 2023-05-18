package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoMessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Profile("mqtt")
@Component
public class MqttListener implements IMqttMessageListener {

    private ObjectMapper mapper = new ObjectMapper();

    private final String MESSAGE_URL = "app/message/";

    private Map<String, IotoMessageHandler> handlers = new HashMap<>();

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {
            String message = new String(mqttMessage.getPayload());
            log.debug("Received message: " + s + " -> " + message );
            IotoMessage iotoMessage = (IotoMessage)parseMessage(message, IotoMessage.class);
            received(iotoMessage, s);
        } catch (Exception ex) {
            log.error("Error processing message: ", ex);
        }
    }

    private void received(IotoMessage message, String destination){
        if (destination.startsWith(MESSAGE_URL)){
            String deviceId = destination.replace(MESSAGE_URL, "");
            if(handlers.containsKey(deviceId)){
                handlers.get(deviceId).onMessageReceived(message);
            }
        } else {
            log.error("Un-known Destination {} ", destination);
        }
    }

    private Object parseMessage(String message, Class<?> clazz){
        try {
            return mapper.readValue(message, clazz);
        } catch (Exception e) {
            log.error("Exception while parsing message ", e);
        }
        return null;
    }

    public void addToMessageHandler(String deviceId, IotoMessageHandler handler){
        handlers.put(deviceId, handler);
    }
}
