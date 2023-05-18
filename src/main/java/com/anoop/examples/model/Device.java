package com.anoop.examples.model;

import com.anoop.examples.enums.DeviceType;
import com.anoop.examples.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
public class Device {
    private String macAddress;
    private String parentId;
    private String userId;
    private String companyId;
    private Status status;
    private String name;
    private String description;
    private DeviceType type;
    private Date lastAvailTime;
    private boolean disable;
}
