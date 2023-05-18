package com.anoop.examples.services.cloud;

import com.anoop.examples.config.TestConfig;
import com.anoop.examples.model.IotoUser;
import com.anoop.examples.model.Measurement;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class, CloudService.class})
@TestPropertySource(properties = {
        "ioto.hub.ms.url=hub_url",
        "ioto.account.ms.url=account_url"
})
public class CloudServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CloudService cloudService;

    @Test
    public void testSendMeasurement(){
        final String deviceId = "device123";
        Measurement result = new Measurement();
        result.setDeviceId(deviceId);
        Measurement[] body = new Measurement[]{result};
        ResponseEntity<Measurement[]> response = new ResponseEntity<>(body, HttpStatus.ACCEPTED);
        when(restTemplate.exchange(
                any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(response);

        List<Measurement> measurements = cloudService.sendMeasurement(List.of(new Measurement()));
        assertNotNull(measurements);
        assertEquals(1, measurements.size());
        assertEquals(deviceId, measurements.get(0).getDeviceId());
    }

    @Test
    public void testGetUser(){
        final String username = "user123";
        IotoUser iotoUserRes = new IotoUser();
        iotoUserRes.setUserName(username);
        ResponseEntity<IotoUser> response = new ResponseEntity<>(iotoUserRes, HttpStatus.ACCEPTED);
        when(restTemplate.getForEntity(anyString(), any(Class.class))).thenReturn(response);

        IotoUser iotoUser = cloudService.getUser();
        assertNotNull(iotoUser);
        assertEquals(username, iotoUser.getUserName());
    }
}
