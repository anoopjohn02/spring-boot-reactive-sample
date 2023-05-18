package com.anoop.examples.services.message;

import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoMessage;

public interface IotoGateway {

    /**
     * Build heart beat message {@link IotoMessage} and send to cloud.
     *
     * @param deviceId
     * @throws Exception
     */
    void sendHeartBeat(String deviceId)throws Exception ;

    /**
     * Build alert message {@link IotoMessage} and send to cloud.
     *
     * @param deviceId
     * @param alert {@link Alert} to be send.
     * @throws Exception
     */
    void sendAlert(String deviceId, Alert alert)throws Exception;

    /**
     * Used to subscribe for the incoming messages.
     *
     * @param deviceId
     * @param handler {@link IotoMessageHandler} to redirect the message
     * @throws Exception
     */
    void subscribe(String deviceId, IotoMessageHandler handler)throws Exception;

    /**
     * Method to connect to messaging service.
     */
    void connect() throws Exception;
    /**
     * Method to check the connection status.
     *
     * @return status of connection
     */
    boolean isConnected();

}
