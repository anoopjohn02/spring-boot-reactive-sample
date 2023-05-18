package com.anoop.examples.services.alerts;

import com.anoop.examples.model.Alert;
import com.anoop.examples.model.IotoUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertCreatedEvent {
    private Alert alert;
    private IotoUser user;
}
