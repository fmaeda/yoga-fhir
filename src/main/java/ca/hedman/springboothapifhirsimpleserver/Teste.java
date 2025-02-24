package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.SingleValidationMessage;
import lombok.SneakyThrows;
import org.hl7.fhir.common.hapi.validation.support.NpmPackageValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.SnapshotGeneratingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.*;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;

public class Teste {

    private static String readResourceAsString(String filePath) {
        try {
            var file = ResourceUtils.getFile("classpath:" + filePath);
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends MetadataResource> T parseStructureDefinition(FhirContext ctx, String jsonPath, Class<T> klazz) {
        IParser parser = ctx.newJsonParser();
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
//        SpringApplication.run(FhirApplication.class, args);


        FhirContext ctx = FhirContext.forR4();
        IParser parser = ctx.newJsonParser();

        var validator = ctx.newValidator();
        var instanceValidator = new FhirInstanceValidator(ctx);

        var valSupport = new PrePopulatedValidationSupport(ctx);
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRIndividuoSD.json", StructureDefinition.class));
        valSupport.addCodeSystem(parseStructureDefinition(ctx, "rnds/BRDivisaoGeograficaBrasilCS.json", CodeSystem.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRDocumentoIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BREnderecoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRJurisdicaoOrgaoEmissorSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRMeioContatoSD.json", StructureDefinition.class));
        valSupport.addValueSet(parseStructureDefinition(ctx, "rnds/BRMunicipioVS.json", ValueSet.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRMunicipioSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRNomeIndividuoSD.json", StructureDefinition.class));
        valSupport.addValueSet(parseStructureDefinition(ctx, "rnds/BRSexoVS.json", ValueSet.class));
        valSupport.addValueSet(parseStructureDefinition(ctx, "rnds/BRUnidadeFederativaVS.json", ValueSet.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRParentesIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRRacaCorEtniaSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRPaisSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRQualidadeCadastroIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRNacionalidadeSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRIndividuoProtegidoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRNaturalizacaoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "rnds/BRNaturalizacaoSD.json", StructureDefinition.class));

//        valSupport.addValueSet(parseStructureDefinition(ctx, "hl7/administrative-gender-vs.json", ValueSet.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/patient-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/extension-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/human-name-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/period-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/codeable-concept-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/coding-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/identifier-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/reference-ds.json", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(ctx, "hl7/contact-point-ds.json", StructureDefinition.class));

        var npmValidationSupport = new NpmPackageValidationSupport(ctx);
        npmValidationSupport.loadPackageFromClasspath("classpath:definitions/terminologias.tgz");

        ValidationSupportChain support = new ValidationSupportChain(
                valSupport,
                npmValidationSupport,
                new DefaultProfileValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx)
        );
        instanceValidator.setValidationSupport(support);
        validator.registerValidatorModule(instanceValidator);

        String input = readResourceAsString("samples/individuo.json");
        Patient parsed = parser.parseResource(Patient.class, input);

        var validationRes = validator.validateWithResult(parsed);
        if (validationRes.isSuccessful()) {
            System.out.println("SUCESSO!!!");
        } else {
            for (SingleValidationMessage msg : validationRes.getMessages()) {
                System.out.println("ERRO: " + msg.getMessage());
            }
        }


//        System.out.println("Parsed: " + parsed.getName().getFirst().getFamily());
//        parsed.addAddress(new Address().setCity("Maringá"));

//        String encoded = parser.setPrettyPrint(true).encodeResourceToString(parsed);
//        System.out.println("Encoded: " + encoded);

//        Header header = new BasicHeader("", "");
//
//        Patient patient = new Patient();
//        patient.addName()
//                .setFamily()
//                .addGiven()
//                .addGiven();
//
//        patient.addExtension("", Type);
    }

}
