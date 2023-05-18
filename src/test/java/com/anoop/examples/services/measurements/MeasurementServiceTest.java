package com.anoop.examples.services.measurements;

import com.anoop.examples.config.TestConfig;
import com.anoop.examples.data.entities.MeasurementEntity;
import com.anoop.examples.data.repos.MeasurementRepository;
import com.anoop.examples.enums.CustomAlertType;
import com.anoop.examples.exceptions.AccessDeniedException;
import com.anoop.examples.exceptions.NotFoundException;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.model.Measurement;
import com.anoop.examples.services.cloud.CloudService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class, MeasurementService.class})
public class MeasurementServiceTest {
    @MockBean
    private MeasurementRepository repository;
    @MockBean
    private ApplicationEventPublisher publisher;
    @MockBean
    private CloudService cloudService;
    @MockBean
    private IotoUser user;

    @Autowired
    private MeasurementService measurementService;

    @Test
    @WithMockUser
    void testCreate() {
        doNothing().when(publisher).publishEvent(any());
        MeasurementEntity entity = new MeasurementEntity();
        entity.set_id(new ObjectId());
        when(user.getRoles()).thenReturn(List.of("DEVICE"));
        when(repository.save(any())).thenReturn(entity);
        when(cloudService.sendMeasurement(any())).thenReturn(List.of());

        Measurement measurement = new Measurement();
        measurement.setDeviceId("test");
        measurement.setValue(1500);

        Measurement result = measurementService.create(user, measurement);
        assertNotNull(result);
        assertNotNull(result.getId());

        verify(repository, times(1)).save(any());
        verify(cloudService, times(1)).sendMeasurement(any());
        ArgumentCaptor<MeasurementCrossedLimitEvent> captor = ArgumentCaptor.forClass(MeasurementCrossedLimitEvent.class);
        verify(publisher, times(1)).publishEvent(captor.capture());
        MeasurementCrossedLimitEvent createdEvent = captor.getValue();
        assertNotNull(createdEvent);
        assertEquals(CustomAlertType.ENERGY_CONSUMPTION_CROSSED, createdEvent.getAlertType());
    }

    @Test
    @WithMockUser
    void testCreateWithNonDevice() {

        when(user.getRoles()).thenReturn(List.of("USER"));

        Measurement measurement = new Measurement();
        measurement.setDeviceId("test");
        measurement.setValue(1500);
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> measurementService.create(user, measurement))
                .withMessage("Access Denied");
    }

    @Test
    void testGet() {
        MeasurementEntity entity = new MeasurementEntity();
        entity.set_id(new ObjectId());
        entity.setDeviceId("test");
        when(repository.findById(any())).thenReturn(Optional.of(entity));

        Measurement result = measurementService.get("63691c4570fdd01e0b300a29");

        assertNotNull(result);
        assertEquals("test", result.getDeviceId());
    }

    @Test
    void testGetByDeviceId() {
        MeasurementEntity entity = new MeasurementEntity();
        entity.set_id(new ObjectId());
        entity.setDeviceId("test");
        when(repository.findByDeviceId(anyString())).thenReturn(List.of(entity));

        List<Measurement> result = measurementService.getByDeviceId("ABCD");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test", result.get(0).getDeviceId());
    }

    @Test
    @WithMockUser
    void testDelete() {
        when(user.getRoles()).thenReturn(List.of("COMPANY_ADMIN"));
        MeasurementEntity entity = new MeasurementEntity();
        entity.set_id(new ObjectId());
        entity.setDeviceId("test");
        when(repository.findById(any())).thenReturn(Optional.of(entity));

        measurementService.delete("63691c4570fdd01e0b300a29", user);

        verify(repository, times(1)).delete(any());
    }

    @Test
    @WithMockUser
    void testDeleteWithInvalidId() {
        when(user.getRoles()).thenReturn(List.of("COMPANY_ADMIN"));
        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> measurementService.delete("63691c4570fdd01e0b300a29", user))
                .withMessage("Measurement Not Found");
    }

    @Test
    @WithMockUser
    void testDeleteWithInvalidUser() {
        when(user.getRoles()).thenReturn(List.of("DEVICE"));
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> measurementService.delete("63691c4570fdd01e0b300a29", user))
                .withMessage("Access Denied");
    }
}
