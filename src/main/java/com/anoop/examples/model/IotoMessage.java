package com.anoop.examples.model;

import com.anoop.examples.enums.MessageType;
import lombok.Data;

@Data
public class IotoMessage {
    private boolean serverNotification;
    private MessageType messageType;
    private String deviceId;
    private Object message;
    private String token;
}
