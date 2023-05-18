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
import reactor.core.publisher.Mono;

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
    public Mono<DeviceDetails> getDeviceDetails(String deviceId, IotoUser user) {
        return Mono.zip(cloudService.getDeviceDetails(user, deviceId),
                        getLocalMeasurements(deviceId),
                        getAlerts(deviceId))
                .log()
                .flatMap(objects -> {
                    DeviceDetails deviceDetails =
                            new DeviceDetails(objects.getT1(), objects.getT2(), objects.getT3());
                    return Mono.just(deviceDetails);
                });
    }

    private Mono<List<Alert>> getAlerts(String deviceId) {
        log.info("Fetching alerts for {}", deviceId);
        return  Mono.zip(
                getLocalAlerts(deviceId),
                cloudService.getDeviceAlerts(deviceId))
                .log()
                .flatMap(objects -> {
                    List<Alert> combined = Stream.concat(objects.getT1().stream(), objects.getT2().stream())
                    .collect(Collectors.toList());
                    return Mono.just(combined);
                });
    }

    private Mono<List<Measurement>> getLocalMeasurements(String deviceId) {
        return Mono.just(measurementService.getByDeviceId(deviceId));
    }

    private Mono<List<Alert>> getLocalAlerts(String deviceId) {
        return Mono.just(alertService.getByDeviceId(deviceId));
    }
}
