package com.anoop.examples.services.cloud;

import com.anoop.examples.exceptions.AccessDeniedException;
import com.anoop.examples.exceptions.ErrorCodes;
import com.anoop.examples.model.Alert;
import com.anoop.examples.model.Device;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.model.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CloudService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ioto.hub.ms.url}")
    private String hubUrl;
    @Value("${ioto.account.ms.url}")
    private String accountUrl;
    @Value("${ioto.connector.ms.url}")
    private String connectorUrl;

    /**
     * Method will send list of {@link Measurement} to hub.
     *
     * @param measurements list of {@link Measurement} to be created
     * @return list of {@link Measurement} created from hub
     */
    public List<Measurement> sendMeasurement(List<Measurement> measurements){

        log.info("Sending measurements size {}", measurements.size());
        HttpEntity<List<Measurement>> request = new HttpEntity<>(measurements);

        String url = hubUrl + "/measurements";
        UriComponents uriComponents =
                UriComponentsBuilder.fromUriString(url)
                        .build().encode();
        log.debug("Accessing URL {}", uriComponents.toUriString());

        ResponseEntity<Measurement[]> response = restTemplate.exchange
                (uriComponents.toUri(), HttpMethod.POST, request, Measurement[].class);
        return Arrays.asList(response.getBody());
    }

    /**
     * Get the current user details.
     *
     * @return {@link IotoUser}
     */
    public IotoUser getUser() {
        String url = accountUrl + "/user/info";
        ResponseEntity<IotoUser> response = restTemplate.getForEntity(url, IotoUser.class);
        return response.getBody();
    }

    /**
     * Get the device details from cloud.
     * <p>{@link #userCanAccessDevice(IotoUser)} will validate whether
     *      * the user can access the device. This permission was set on API.</p>
     *
     * @param user which trying to access device
     * @param deviceId to be fetched
     * @return {@link Device}
     */
    @PreAuthorize("@cloudService.userCanAccessDevice(#user)")
    public Device getDeviceDetails(IotoUser user, String deviceId) {
        log.info("Fetching device details for {}", deviceId);
        String url = hubUrl + "/remote/{deviceId}/CUSTOM_TYPE";
        UriComponents uriComponents =
                UriComponentsBuilder.fromUriString(url)
                        .build().expand(deviceId).encode();

        ResponseEntity<Device> response = restTemplate.getForEntity(uriComponents.toUri(), Device.class);
        return response.getBody();
    }

    /**
     * Get the device alerts from cloud.
     *
     * @param deviceId device id
     * @return list of {@link Alert}
     */
    public List<Alert> getDeviceAlerts(String deviceId) {
        log.info("Fetching cloud alerts for {}", deviceId);
        String url = connectorUrl + "/alert/{deviceId}";
        UriComponents uriComponents =
                UriComponentsBuilder.fromUriString(url)
                        .build().expand(deviceId).encode();
        ResponseEntity<List<Alert>> response = restTemplate.exchange(
                uriComponents.toUri(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Alert>>() {});
        return response.getBody();
    }

    public boolean userCanAccessDevice(IotoUser user) {
        if(user.getRoles().contains("DEVICE")) {
            return true;
        }
        throw new AccessDeniedException(ErrorCodes.ACCESS_DENIED);
    }
}
