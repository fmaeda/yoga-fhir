# Spring Boot HAPI-FHIR Plain Server

This project aims to be a minimal skeleton that sets up a [Spring Boot](https://spring.io/projects/spring-boot) server
as a [HAPI-FHIR](https://hapifhir.io/hapi-fhir/) server ([Plain Server](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html).)

A single [Resource Provider](https://bitbucket.org/BrunoHedman/spring-boot-hapi-fhir/src/master/src/main/java/ca/hedman/springboothapifhirsimpleserver/provider/PatientProvider.java) exists (Patient), serving up 3 hard-coded patients.

Execute SpringBootHapiFhirSimpleServerApplicationTests to see it in action.

Alternately, you can launch the server using SpringBootHapiFhirSimpleServerApplication and 
point your browser to e.g. the following URL:

http://localhost:8080/fhir/Patient/2


