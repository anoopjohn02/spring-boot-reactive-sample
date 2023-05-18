package com.anoop.examples.enums;

public enum CustomAlertType {
    ENERGY_CONSUMPTION_CROSSED("app.notification.device.energy.usage.high","Energy Usage Crossed the limit");
    private final String key;
    private final String description;

    private CustomAlertType(String key, String description){

        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
