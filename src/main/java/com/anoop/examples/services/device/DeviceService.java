package com.anoop.examples.services.device;

import com.anoop.examples.model.Alert;
import com.anoop.examples.model.Device;
import com.anoop.examples.model.DeviceDetails;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.model.Measurement;
import com.anoop.examples.services.alerts.AlertService;
import com.anoop.examples.services.cloud.CloudService;
import com.anoop.examples.services.measurements.MeasurementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DeviceService {

    @Autowired
    private CloudService cloudService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private MeasurementService measurementService;

    /**
     * The method to fetch complete device details.
     *
     * @param user logged in user
     * @param deviceId Device Id
     * @return {@link DeviceDetails}
     */
    public DeviceDetails getDeviceDetails(String deviceId, IotoUser user) {
        Device device = cloudService.getDeviceDetails(user, deviceId);
        List<Measurement> measurements = measurementService.getByDeviceId(deviceId);
        List<Alert> alerts = getAlerts(deviceId);
        return new DeviceDetails(device, measurements, alerts);
    }

    private List<Alert> getAlerts(String deviceId) {
        List<Alert> local = alertService.getByDeviceId(deviceId);
        List<Alert> cloud = cloudService.getDeviceAlerts(deviceId);
        return Stream.concat(local.stream(), cloud.stream())
                .collect(Collectors.toList());
    }
}
