package com.anoop.examples.services.message;

import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeviceMessagingService implements IotoMessageHandler {

    @Value("${ioto.login.userId}")
    private String userId;

    @Autowired
    private IotoGateway gateway;

    /**
     * Method will send device heart-beat in every 5 seconds.
     */
    //@Scheduled(fixedDelay = 5000)
    public void sendHeartBeat() {
        try {
            if(!gateway.isConnected()){
                gateway.connect();
            }
            log.info("Sending heart-beat....");
            gateway.sendHeartBeat(userId);
        } catch (Exception exception) {
            log.error("Error while sending heart beat", exception);
        }
    }

    @Override
    public void onMessageReceived(IotoMessage message) {
        log.info("Message arrived ");
    }

    /**
     * Method will redirect the alert message to gateway.
     *
     * @param deviceId - Device Id where the alert generated.
     * @param alert {@link Alert} to be send.
     * @throws Exception
     */
    public void sendAlert(String deviceId, Alert alert) throws Exception {
        gateway.sendAlert(deviceId, alert);
    }
}
