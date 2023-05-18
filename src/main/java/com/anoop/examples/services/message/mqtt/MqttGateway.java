package com.anoop.examples.services.message.mqtt;

import com.anoop.examples.enums.MessageType;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoMessage;
import com.anoop.examples.services.message.IotoGateway;
import com.anoop.examples.services.message.IotoMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("mqtt")
@Service
public class MqttGateway implements IotoGateway {

    @Autowired
    private MqttConnection connection;

    @Override
    public void sendHeartBeat(String deviceId) throws Exception {
        if (isConnected()) {
            log.debug("Sending heartbeat {}", deviceId);
            IotoMessage message = new IotoMessage();
            message.setDeviceId(deviceId);
            message.setMessageType(MessageType.HEART_BEAT);
            connection.sendMessage(deviceId, message);
        } else {
            log.debug("MQTT not connected");
        }
    }

    @Override
    public void sendAlert(String deviceId, Alert alert) throws Exception {
        if (isConnected()) {
            log.debug("Sending Alert {}", deviceId);
            IotoMessage message = new IotoMessage();
            message.setDeviceId(alert.getDeviceId());
            message.setMessageType(MessageType.ALERT);
            message.setMessage(alert);
            connection.sendMessage(deviceId, message);
        } else {
            log.debug("MQTT not connected");
        }
    }

    @Override
    public void subscribe(String deviceId, IotoMessageHandler handler) throws Exception {
        connection.subscribe(deviceId, handler);
    }

    @Override
    public void connect() throws Exception {
        connection.connect();
    }

    @Override
    public boolean isConnected() {
        return connection.connected();
    }

}
