package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.SingleValidationMessage;
import lombok.SneakyThrows;
import org.hl7.fhir.common.hapi.validation.support.*;
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
        var xmlParser = ctx.newXmlParser();
        var jsonParser = ctx.newJsonParser();

        var validator = ctx.newValidator();
        var instanceValidator = new FhirInstanceValidator(ctx);

        var valSupport = new PrePopulatedValidationSupport(ctx);
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/BRIndividuo.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/Extension/BRParentesIndividuo.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/Extension/BRRacaCorEtnia.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/Extension/BRPais.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/DataType/BRDocumentoIndividuo.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/DataType/BRNomeIndividuo.StructureDefinition.xml", StructureDefinition.class));
//        valSupport.addStructureDefinition(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/DataType/BREndereco.StructureDefinition.xml", StructureDefinition.class));

//        valSupport.addValueSet(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/ValueSet/BRPais.ValueSet.xml", ValueSet.class));
//        valSupport.addValueSet(parseStructureDefinition(jsonParser, "hl7/administrative-gender-vs.json", ValueSet.class));
//        valSupport.addCodeSystem(parseStructureDefinition(jsonParser, "hl7/administrative-gender-cs.json", CodeSystem.class));
//        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRSexoVS.json", ValueSet.class));
//        valSupport.addValueSet(parseStructureDefinition(xmlParser, "rnds-definicoes-fhir/ValueSet/BRSexo.ValueSet.xml", ValueSet.class));

        valSupport.addCodeSystem(parseStructureDefinition(jsonParser, "rnds/BRPaisCS.json", CodeSystem.class));
        valSupport.addCodeSystem(parseStructureDefinition(jsonParser, "rnds/BRDivisaoGeograficaBrasilCS.json", CodeSystem.class));

        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRMunicipioVS.json", ValueSet.class));
        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRUnidadeFederativaVS.json", ValueSet.class));
        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRPaisVS.json", ValueSet.class));
        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRTipoDocumentoIndividuoVS.json", ValueSet.class));
        valSupport.addValueSet(parseStructureDefinition(jsonParser, "rnds/BRSexoVS.json", ValueSet.class));

        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BREnderecoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRRacaCorEtniaSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRPaisSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRDocumentoIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRNomeIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRQualidadeCadastroIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRParentesIndividuoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRMunicipioSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRNacionalidadeSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRIndividuoProtegidoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRNaturalizacaoSD.json", StructureDefinition.class));
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRJurisdicaoOrgaoEmissorSD.json", StructureDefinition.class)); // extension
        valSupport.addStructureDefinition(parseStructureDefinition(jsonParser, "rnds/BRMeioContatoSD.json", StructureDefinition.class));

        ValidationSupportChain support = new ValidationSupportChain(
                valSupport,
                new InMemoryTerminologyServerValidationSupport(ctx),
                new DefaultProfileValidationSupport(ctx),
                new SnapshotGeneratingValidationSupport(ctx)
        );
        instanceValidator.setValidationSupport(support);
        validator.registerValidatorModule(instanceValidator);

        String input = readResourceAsString("samples/individuo.json");
        Patient parsed = jsonParser.parseResource(Patient.class, input);

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
