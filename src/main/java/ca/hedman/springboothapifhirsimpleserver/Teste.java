package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import lombok.SneakyThrows;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.profilemodel.gen.PECodeGenerator;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Teste {
    private static final String LOG_VALIDATION_MESSAGE_TEMPLATE = "%s at %s - %s";

    private static String readResourceAsString(String filePath) {
        try {
            var file = ResourceUtils.getFile("classpath:" + filePath);
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends MetadataResource> T parseStructureDefinition(IParser parser, String jsonPath, Class<T> klazz) {
        try {
            File file = ResourceUtils.getFile("classpath:definitions/" + jsonPath);
            InputStream in = new FileInputStream(file);
            return parser.parseResource(klazz, in);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        FhirContext ctx = FhirContext.forR4();

        var authInterceptor = new BasicAuthInterceptor("fmaeda", "QZK!neu7bxt7wqu5fcv");
        var remoteTerminologyService = new RemoteTerminologyServiceValidationSupport(ctx, "https://fhir.loinc.org");
        remoteTerminologyService.addClientInterceptor(authInterceptor);

        var jsonParser = ctx.newJsonParser();
        var validator = ctx.newValidator();
        var instanceValidator = new FhirInstanceValidator(ctx);
        var valSupport = new PrePopulatedValidationSupport(ctx);
//        addJSONDefinitions(valSupport, ctx);

        var npmPackageSupport = new NpmPackageValidationSupport(ctx);
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/ans.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/brcore.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/ips.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/rnds.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/terminologias.tgz");

        var supportChain = new ValidationSupportChain(
                new DefaultProfileValidationSupport(ctx),
                valSupport,
                new InMemoryTerminologyServerValidationSupport(ctx),
                npmPackageSupport,
                remoteTerminologyService
//                new SnapshotGeneratingValidationSupport(ctx)
        );
        instanceValidator.setValidationSupport(supportChain);
        validator.registerValidatorModule(instanceValidator);

        Bundle parsedJson = jsonParser.parseResource(Bundle.class, readResourceAsString("rac-output.json"));
//        Patient parsedJson = jsonParser.parseResource(Patient.class, readResourceAsString("samples/patient-br.json"));

        var validationRes = validator.validateWithResult(parsedJson);
        final Consumer<ResultSeverityEnum> logger = (severity) -> validationRes.getMessages().stream()
                .filter(message -> message.getSeverity().equals(severity))
                .forEach(message -> System.out.printf((LOG_VALIDATION_MESSAGE_TEMPLATE) + "%n", message.getSeverity(), message.getLocationString(), message.getMessage()));
        logger.accept(ResultSeverityEnum.INFORMATION);
        logger.accept(ResultSeverityEnum.WARNING);
        logger.accept(ResultSeverityEnum.ERROR);
        logger.accept(ResultSeverityEnum.FATAL);

        System.out.println("QUANTIDADE DE ERROS: " + validationRes.getMessages().stream().filter(message -> message.getSeverity().equals(ResultSeverityEnum.ERROR)).count() + " de " + validationRes.getMessages().size());
    }

//    private static void addJSONDefinitions(PrePopulatedValidationSupport valSupport, FhirContext ctx) {
//        var jsonParser = ctx.newJsonParser();
//        var xmlParser = ctx.newXmlParser();
//
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-allergyintolerance.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-capacidadefuncional.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-careplan.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-careteam.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-composition.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-condition.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-diagnosticreport.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-encounter.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-healthcareservice.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-immunization.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-location.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-medication.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-medicationadministration.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-medicationdispense.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-medicationrequest.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-medicationstatement.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observation.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationalcoholuse.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationbreastfeedingstatus.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationpregnancyedd.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationpregnancyoutcome.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationpregnancystatus.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-observationtobaccouse.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-organization.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-patient.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-practitioner.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-practitionerrole.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-procedure.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-registroatendimentoclinico.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-relatedperson.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-servicerequest.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-specimen.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-sumarioalta.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "br-core/StructureDefinition-br-core-vitalsigns.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "ips-brasil/StructureDefinition-raca-br-ips.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "ips-brasil/StructureDefinition-sexo-nascimento-br-ips.json", StructureDefinition.class));
////        valSupport.addCodeSystem(parseStructureDefinition(xmlParser, "loinc/loinc.xml", CodeSystem.class));
//    }

}
