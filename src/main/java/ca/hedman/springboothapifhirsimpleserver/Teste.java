package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import lombok.SneakyThrows;
import org.hl7.fhir.common.hapi.validation.support.*;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.profilemodel.gen.PECodeGenerator;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
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

    @SneakyThrows
    public static void main(String[] args) {
        FhirContext ctx = FhirContext.forR4();

//        var authInterceptor = new BasicAuthInterceptor("fmaeda", "QZK!neu7bxt7wqu5fcv");
//        var remoteTerminologyService = new RemoteTerminologyServiceValidationSupport(ctx, "https://fhir.loinc.org");
//        remoteTerminologyService.addClientInterceptor(authInterceptor);

        var jsonParser = ctx.newJsonParser();
        var validator = ctx.newValidator();
        var instanceValidator = new FhirInstanceValidator(ctx);
        var valSupport = new PrePopulatedValidationSupport(ctx);
        loadProfiles(jsonParser, valSupport, "/br-core");

        var npmPackageSupport = new NpmPackageValidationSupport(ctx);
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/ans.tgz");
//        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/brcore.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/ips.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/rnds.tgz");
        npmPackageSupport.loadPackageFromClasspath("classpath:definitions/terminologias.tgz");

        var supportChain = new ValidationSupportChain(
                new DefaultProfileValidationSupport(ctx),
                valSupport,
                new InMemoryTerminologyServerValidationSupport(ctx),
                npmPackageSupport//,
//                remoteTerminologyService
//                new SnapshotGeneratingValidationSupport(ctx)
        );
        instanceValidator.setValidationSupport(supportChain);
        validator.registerValidatorModule(instanceValidator);

        Bundle parsedJson = jsonParser.parseResource(Bundle.class, readResourceAsString("rac-output.json"));
//        Patient parsedJson = jsonParser.parseResource(Patient.class, readResourceAsString("samples/patient-br.json"));

        StopWatch sw = new StopWatch();
        System.out.println("====>INICIANDO VALIDACAO");
        sw.start();
        var validationRes = validator.validateWithResult(parsedJson);
        final Consumer<ResultSeverityEnum> logger = (severity) -> validationRes.getMessages().stream()
                .filter(message -> message.getSeverity().equals(severity))
                .forEach(message -> System.out.printf((LOG_VALIDATION_MESSAGE_TEMPLATE) + "%n", message.getSeverity(), message.getLocationString(), message.getMessage()));
        logger.accept(ResultSeverityEnum.INFORMATION);
        logger.accept(ResultSeverityEnum.WARNING);
        logger.accept(ResultSeverityEnum.ERROR);
        logger.accept(ResultSeverityEnum.FATAL);

        sw.stop();
        System.out.println("QUANTIDADE DE ERROS: " + validationRes.getMessages().stream().filter(message -> message.getSeverity().equals(ResultSeverityEnum.ERROR)).count() + " de " + validationRes.getMessages().size() + "(" + sw.getTotalTimeMillis() + "ms)");
    }


    @SneakyThrows
    private static void loadProfiles(IParser parser, PrePopulatedValidationSupport validationSupport, String folderPath) {
        var folder = ResourceUtils.getFile("classpath:definitions" + folderPath);
        for (var file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().contains("StructureDefinition")) {
                var sd = parser.parseResource(StructureDefinition.class, new FileInputStream(file));
                validationSupport.addStructureDefinition(sd);
                System.out.println("Loaded: " + sd.getUrl());
            } else {
                System.out.println("Skipped " + file.getName());
            }
        }
    }
}
