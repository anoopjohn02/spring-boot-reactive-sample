package com.anoop.examples.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeviceDetails {
    private Device device;
    private List<Measurement> measurements;
    private List<Alert> alerts;
}
