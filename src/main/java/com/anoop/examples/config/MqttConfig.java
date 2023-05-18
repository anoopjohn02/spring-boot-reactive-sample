package com.anoop.examples.config;

import com.anoop.examples.services.message.mqtt.MqttConnection;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile("mqtt")
@Configuration
public class MqttConfig {

    @Value("${ioto.mqtt.server.url}")
    private String host;

    @Value("${ioto.login.userId}")
    private String userName;

    @Bean
    public IMqttClient mqttClient() throws Exception {
        MqttClient mqttClient = new MqttClient(host, MqttConnection.MQTT_PUBLISHER_ID + userName);
        mqttClient.setTimeToWait(5000);
        return mqttClient;
    }
}
