package com.anoop.examples.services.alerts;

import com.anoop.examples.data.entities.AlertEntity;
import com.anoop.examples.data.repos.AlertRepository;
import com.anoop.examples.exceptions.AccessDeniedException;
import com.anoop.examples.exceptions.ErrorCodes;
import com.anoop.examples.exceptions.NotFoundException;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * Method to create alert.
     * <p>{@link #userCanCreateAlert(IotoUser)} will validate whether
     * the user can have to create an alert.</p>
     *
     * @param alert to be created
     * @param user  the logged-in user
     * @return created {@link Alert} Object
     */
    @Transactional
    @PreAuthorize("@alertService.userCanCreateAlert(#user)")
    public Alert create(Alert alert, IotoUser user) {
        AlertEntity entity = modelMapper.map(alert, AlertEntity.class);
        entity = alertRepository.save(entity);
        alert.setUId(entity.get_id().toString());
        publisher.publishEvent(new AlertCreatedEvent(alert, user));
        return getAlert(entity);
    }

    /**
     * Method to get a single alert.
     *
     * @param id the alert id
     * @return {@link Alert} Object
     */
    public Alert get(String id) {
        AlertEntity alertEntity = getEntity(id);
        return getAlert(alertEntity);
    }

    /**
     * Method to get a list of {@link Alert} for a device.
     *
     * @param deviceId the device id
     * @return list of {@link Alert}
     */
    public List<Alert> getByDeviceId(String deviceId) {
        List<AlertEntity> entities = alertRepository.findByDeviceId(deviceId);
        return entities.stream()
                .map(this::getAlert)
                .collect(Collectors.toList());
    }

    /**
     * Method to delete an alert.
     * <p> {@link #userCanDeleteAlert(IotoUser)} will validate whether the user
     * have permission to delete an alert.</p>
     *
     * @param id   the alert id
     * @param user the logged-in user
     */
    @Transactional
    @PreAuthorize("@alertService.userCanDeleteAlert(#user)")
    public void delete(String id, IotoUser user) {
        AlertEntity alertEntity = getEntity(id);
        alertRepository.delete(alertEntity);
    }

    public boolean userCanCreateAlert(IotoUser user) {
        if(user.getRoles().contains("DEVICE")) {
            return true;
        }
        throw new AccessDeniedException(ErrorCodes.ACCESS_DENIED);
    }

    public boolean userCanDeleteAlert(IotoUser user) {
        if(user.getRoles().contains("COMPANY_ADMIN")) {
            return true;
        }
        throw new AccessDeniedException(ErrorCodes.ACCESS_DENIED);
    }

    private AlertEntity getEntity(String id) {
        Optional<AlertEntity> entityOpt = alertRepository.findById(new ObjectId(id));
        if (!entityOpt.isPresent()) {
            throw new NotFoundException(ErrorCodes.ALERT_NOT_FOUND);
        }
        return entityOpt.get();
    }

    private Alert getAlert(AlertEntity entity) {
        Alert alert = modelMapper.map(entity, Alert.class);
        String id = entity.get_id().toString();
        alert.setId(id);
        return alert;
    }
}
