package com.anoop.examples.services.measurements;

import com.anoop.examples.enums.CustomAlertType;
import com.anoop.examples.model.IotoUser;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeasurementCrossedLimitEvent {

    private IotoUser user;
    private CustomAlertType alertType;
}
