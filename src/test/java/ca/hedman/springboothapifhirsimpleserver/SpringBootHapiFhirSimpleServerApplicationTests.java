package ca.hedman.springboothapifhirsimpleserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootHapiFhirSimpleServerApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void metadata() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity(
                "/fhir/metadata",
                String.class);
        assert (entity.getStatusCode().equals(HttpStatus.OK));
        assert (entity.getBody()).contains("\"status\": \"active\"");
    }

    @Test
    public void patientResource() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity(
                "/fhir/Patient/1",
                String.class);
        assert (entity.getStatusCode().equals(HttpStatus.OK));
        assert (entity.getBody()).contains("\"family\": \"Stitt\"");
    }

}
