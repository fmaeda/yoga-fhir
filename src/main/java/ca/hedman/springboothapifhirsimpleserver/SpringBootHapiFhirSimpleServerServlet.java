package ca.hedman.springboothapifhirsimpleserver;

import ca.hedman.springboothapifhirsimpleserver.provider.PatientProvider;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.CorsInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = "/fhir/*", loadOnStartup = 1)
public class SpringBootHapiFhirSimpleServerServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    private Environment env;

    @Autowired
    SpringBootHapiFhirSimpleServerServlet(Environment environment) {
        this.env = environment;
    }

    @Override
    public void initialize() {

        /*
         Add Resource Providers
         */
        List<IResourceProvider> providers = new ArrayList<>();
        providers.add(new PatientProvider());
        setResourceProviders(providers);

        /*
        Add Interceptors
         */
        registerInterceptor(new CustomLoggingInterceptor()); // your logging
        registerInterceptor(new ResponseHighlighterInterceptor()); // enable viewing in browser

        /*
        Various config
         */
        setDefaultResponseEncoding(EncodingEnum.JSON);
        setImplementationDescription("Spring Boot HAPI-FHIR (R4) Simple Server");
        setDefaultPrettyPrint(true);

    }
}

