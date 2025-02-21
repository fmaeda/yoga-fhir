package ca.hedman.springboothapifhirsimpleserver;

import ca.hedman.springboothapifhirsimpleserver.provider.PatientProvider;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

import ca.uhn.fhir.validation.FhirValidator;
import jakarta.servlet.annotation.WebServlet;
import lombok.SneakyThrows;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.StructureDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/*", loadOnStartup = 1)
public class SpringBootHapiFhirSimpleServerServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    @SneakyThrows
    private StructureDefinition loadedStructureDefinition() {
        var parser = getFhirContext().newJsonParser();
        var file = ResourceUtils.getFile("classpath:definitions/BrIndividuo.json");
        var in = new FileInputStream(file);
        return parser.parseResource(StructureDefinition.class, in);
    }

    @Bean
    public FhirValidator getValidator() {
        var ctx = getFhirContext();
        var validator = ctx.newValidator();

        var structureDefinition = loadedStructureDefinition();
        var valSupport = new PrePopulatedValidationSupport(ctx);
        valSupport.addStructureDefinition(structureDefinition);

        var instanceValidator = new FhirInstanceValidator(ctx);
        var support = new ValidationSupportChain(valSupport, new DefaultProfileValidationSupport(ctx));
        instanceValidator.setValidationSupport(support);
        validator.registerValidatorModule(instanceValidator);

        return validator;
    }

    @Override
    public void initialize() {

        /*
         Add Resource Providers
         */
        List<IResourceProvider> providers = new ArrayList<>();
        providers.add(new PatientProvider(getFhirContext()));
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


        getFhirContext().setParserErrorHandler(new StrictErrorHandler());

        // custom profiles
//        getFhirContext().validator
    }
}

