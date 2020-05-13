package ca.hedman.springboothapifhirsimpleserver;

import ca.hedman.springboothapifhirsimpleserver.provider.PatientProvider;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/fhir/*", loadOnStartup = 1)
public class SpringBootHapiFhirSimpleServerServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

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

