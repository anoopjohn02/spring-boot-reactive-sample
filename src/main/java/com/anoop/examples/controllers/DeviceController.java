package com.anoop.examples.controllers;

import com.anoop.examples.config.InjectIotoUser;
import com.anoop.examples.model.DeviceDetails;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.services.device.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService  deviceService;

    @Operation(summary = "Returns device details.")
    @ApiResponse(
            responseCode = "200",
            description = "Device Details")
    @GetMapping(value = {"/{deviceId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public DeviceDetails getDeviceDetails(@PathVariable(value = "deviceId") String deviceId,
                                          @InjectIotoUser IotoUser user) {
        return deviceService.getDeviceDetails(deviceId, user);
    }
}
