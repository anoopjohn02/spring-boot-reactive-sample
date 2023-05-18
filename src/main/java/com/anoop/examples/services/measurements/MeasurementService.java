package com.anoop.examples.services.measurements;

import com.anoop.examples.data.entities.MeasurementEntity;
import com.anoop.examples.data.repos.MeasurementRepository;
import com.anoop.examples.enums.CustomAlertType;
import com.anoop.examples.exceptions.AccessDeniedException;
import com.anoop.examples.exceptions.ErrorCodes;
import com.anoop.examples.exceptions.NotFoundException;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.model.Measurement;
import com.anoop.examples.services.cloud.CloudService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MeasurementService {

    @Value("${ioto.login.userId}")
    private String userId;
    @Autowired
    private MeasurementRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private CloudService cloudService;

    private IotoUser device;

    /**
     * Method will send a random measurement in every 15 mins.
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void sendEnergyMeasurement() {
        try {
            if(device == null){
                log.info("Fetching device...");
                device = cloudService.getUser();
            }

            int energy = getRandomNumbers(500, 1500);
            Measurement measurement = new Measurement();
            measurement.setDeviceId(userId);
            measurement.setEnd(new Date());
            measurement.setStart(Date.from(getStartDate(15).toInstant()));
            measurement.setMetadata(new HashMap());
            measurement.setValue(energy);
            measurement.setUnit("Wh");
            measurement.setType("ENERGY_CONSUMPTION");
            create(device, measurement);
        } catch (Exception ex) {
            log.error("Error while Sending Measurement ", ex);
        }
    }

    /**
     * Method with save the measurement into DB and send to cloud.
     * <p>If the {@link Measurement#getValue()} > 1200 this will publish
     * {@link MeasurementCrossedLimitEvent}.</p>
     * <p>{@link #userCanCreateMeasurement(IotoUser)} will validate whether the user have
     * permission to create a measurement.</p>
     *
     * @param user logged-in user
     * @param measurement {@link Measurement} to be created
     * @return Created {@link Measurement} with id
     */
    @Transactional
    @PreAuthorize("@measurementService.userCanCreateMeasurement(#user)")
    public Measurement create(IotoUser user, Measurement measurement) {
        if (measurement.getValue() > 1200){
            publisher.publishEvent(
                    new MeasurementCrossedLimitEvent(user, CustomAlertType.ENERGY_CONSUMPTION_CROSSED));
        }

        MeasurementEntity entity = modelMapper.map(measurement, MeasurementEntity.class);
        entity = repository.save(entity);

        cloudService.sendMeasurement(List.of(measurement));
        return getMeasurement(entity);
    }

    public boolean userCanCreateMeasurement(IotoUser user) {
        if(user.getRoles().contains("DEVICE")) {
            return true;
        }
        throw new AccessDeniedException(ErrorCodes.ACCESS_DENIED);
    }

    /**
     * Method to get a single {@link Measurement}
     *
     * @param id the measurement Id
     * @return {@link Measurement}
     */
    public Measurement get(String id) {
        MeasurementEntity entity = getEntity(id);
        return getMeasurement(entity);
    }

    /**
     * Method to get a list of {@link Measurement} for a device.
     *
     * @param deviceId the device id
     * @return list of {@link Measurement}
     */
    public List<Measurement> getByDeviceId(String deviceId) {
        List<MeasurementEntity> entities = repository.findByDeviceId(deviceId);
        return entities.stream()
                .map(this::getMeasurement)
                .collect(Collectors.toList());
    }

    /**
     * Method to delete a measurement.
     * <p>{@link #userCanDeleteMeasurement(IotoUser)} will validate
     * whether the user can permission to delete.</p>
     *
     * @param id   the measurement id
     * @param user the logged-in user
     */
    @Transactional
    @PreAuthorize("@measurementService.userCanDeleteMeasurement(#user)")
    public void delete(String id, IotoUser user) {
        MeasurementEntity entity = getEntity(id);
        repository.delete(entity);
    }

    public boolean userCanDeleteMeasurement(IotoUser user) {
        if(user.getRoles().contains("COMPANY_ADMIN")) {
            return true;
        }
        throw new AccessDeniedException(ErrorCodes.ACCESS_DENIED);
    }

    private MeasurementEntity getEntity(String id) {
        Optional<MeasurementEntity> entityOpt = repository.findById(new ObjectId(id));
        if (!entityOpt.isPresent()) {
            throw new NotFoundException(ErrorCodes.MEASUREMENT_NOT_FOUND);
        }
        return entityOpt.get();
    }

    private ZonedDateTime getStartDate(int minute) {
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault());
        if (now.getMinute() >= minute) {
            return now.withMinute(minute);
        } else {
            return now.withMinute(0);
        }
    }

    private int getRandomNumbers(int low, int high) {
        Random random = new Random();
        int result = random.nextInt(high - low) + low;
        return result;
    }

    private Measurement getMeasurement(MeasurementEntity entity) {
        Measurement measurement = modelMapper.map(entity, Measurement.class);
        String id = entity.get_id().toString();
        measurement.setId(id);
        return measurement;
    }

}
