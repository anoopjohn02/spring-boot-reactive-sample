package com.anoop.examples.model;

import com.anoop.examples.enums.Severity;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Alert {
    private String id;
    private String uId;
    private String deviceId;
    private String type;
    private Severity severity;
    private String key;
    private String description;
    private Date dateTime;
    private Map<String, Object> metadata;
}
