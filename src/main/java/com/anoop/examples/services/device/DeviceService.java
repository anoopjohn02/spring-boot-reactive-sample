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
        Mono<Device> device = Mono.just(cloudService.getDeviceDetails(user, deviceId));
        Mono<List<Measurement>> measurements = Mono.just(measurementService.getByDeviceId(deviceId));
        Mono<List<Alert>> alerts = getAlerts(deviceId);
        return Mono.zip(device, measurements, alerts)
                .flatMap(objects -> {
                    DeviceDetails deviceDetails =
                            new DeviceDetails(objects.getT1(), objects.getT2(), objects.getT3());
                    return Mono.just(deviceDetails);
                });
    }

    private Mono<List<Alert>> getAlerts(String deviceId) {
        log.info("Fetching alerts for {}", deviceId);
        Mono<List<Alert>> local = Mono.just(alertService.getByDeviceId(deviceId));
        Mono<List<Alert>> cloud = Mono.just(cloudService.getDeviceAlerts(deviceId));

        return  Mono.zip(local, cloud).flatMap(objects -> {
            List<Alert> combined = Stream.concat(objects.getT1().stream(), objects.getT2().stream())
                    .collect(Collectors.toList());
            return Mono.just(combined);
        });
    }
}
