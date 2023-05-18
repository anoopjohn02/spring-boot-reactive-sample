package com.anoop.examples.services.alerts;

import com.anoop.examples.config.TestConfig;
import com.anoop.examples.data.entities.AlertEntity;
import com.anoop.examples.data.repos.AlertRepository;
import com.anoop.examples.enums.Severity;
import com.anoop.examples.exceptions.AccessDeniedException;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

@SpringBootTest(classes = {TestConfig.class, AlertService.class})
class AlertServiceTest {

    @MockBean
    private JwtDecoder jwtDecoder;
    @MockBean
    private AlertRepository alertRepository;
    @MockBean
    private ApplicationEventPublisher publisher;
    @MockBean
    private IotoUser user;

    @Autowired
    private AlertService alertService;

    @Test
    @WithMockUser
    void testCreate() {
        doNothing().when(publisher).publishEvent(any());
        AlertEntity entity = new AlertEntity();
        entity.set_id(new ObjectId());
        when(user.getRoles()).thenReturn(List.of("DEVICE"));
        when(alertRepository.save(any())).thenReturn(entity);

        Alert alert = new Alert();
        alert.setDescription("test");
        alert.setSeverity(Severity.MAJOR);

        Alert result = alertService.create(alert, user);
        assertNotNull(result);
        assertNotNull(result.getId());

        verify(alertRepository, times(1)).save(any());
        ArgumentCaptor<AlertCreatedEvent> captor = ArgumentCaptor.forClass(AlertCreatedEvent.class);
        verify(publisher, times(1)).publishEvent(captor.capture());
        AlertCreatedEvent createdEvent = captor.getValue();
        assertNotNull(createdEvent.getAlert());
        assertEquals("test", createdEvent.getAlert().getDescription());
    }

    @Test
    @WithMockUser
    void testCreateWithNonDevice() {

        when(user.getRoles()).thenReturn(List.of("USER"));

        Alert alert = new Alert();
        alert.setDescription("test");
        alert.setSeverity(Severity.MAJOR);
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> alertService.create(alert, user))
                .withMessage("Access Denied");
    }

    @Test
    void testGet() {
        AlertEntity entity = new AlertEntity();
        entity.set_id(new ObjectId());
        entity.setDescription("test");
        when(alertRepository.findById(any())).thenReturn(Optional.of(entity));

        Alert result = alertService.get("63691c4570fdd01e0b300a29");

        assertNotNull(result);
        assertEquals("test", result.getDescription());
    }

    @Test
    void testGetByDeviceId() {
        AlertEntity entity = new AlertEntity();
        entity.set_id(new ObjectId());
        entity.setDescription("test");
        when(alertRepository.findByDeviceId(anyString())).thenReturn(List.of(entity));

        List<Alert> result = alertService.getByDeviceId("ABCD");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test", result.get(0).getDescription());
    }

    @Test
    @WithMockUser
    void testDelete() {
        when(user.getRoles()).thenReturn(List.of("COMPANY_ADMIN"));
        AlertEntity entity = new AlertEntity();
        entity.set_id(new ObjectId());
        entity.setDescription("test");
        when(alertRepository.findById(any())).thenReturn(Optional.of(entity));

        alertService.delete("63691c4570fdd01e0b300a29", user);

        verify(alertRepository, times(1)).delete(any());
    }

    @Test
    @WithMockUser
    void testDeleteWithInvalidUser() {
        when(user.getRoles()).thenReturn(List.of("DEVICE"));
        AlertEntity entity = new AlertEntity();
        entity.set_id(new ObjectId());
        entity.setDescription("test");
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> alertService.delete("63691c4570fdd01e0b300a29", user))
                .withMessage("Access Denied");
    }
}