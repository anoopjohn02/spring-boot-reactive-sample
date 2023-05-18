package com.anoop.examples.services.message;

import com.anoop.examples.model.IotoMessage;

public interface IotoMessageHandler {
    /**
     * Method will call when a message arrived at Gateway.
     * @param message the received message {@link IotoMessage}
     */
    void onMessageReceived(IotoMessage message);
}
