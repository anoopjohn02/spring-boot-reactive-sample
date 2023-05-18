package com.anoop.examples.model;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Measurement {
    private String id;
    private String deviceId;
    private String type;
    private long value;
    private String unit;
    private Date start;
    private Date end;
    private long timeDiffInMillis;
    private Map<String, Object> metadata;
}
