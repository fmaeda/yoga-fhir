package ca.hedman.springboothapifhirsimpleserver;

import br.com.libertyti.yogafhir.factory.PatientFactory;
import br.com.libertyti.yogafhir.factory.RacBundleFactory;
import br.com.libertyti.yogafhir.model.IdentidadeGenero;
import br.com.libertyti.yogafhir.model.Paciente;
import br.com.libertyti.yogafhir.model.RacaCor;
import br.com.libertyti.yogafhir.model.Sexo;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.Bundle;

import java.util.List;

public class GerarJsonRAC {

    public static void main(String[] args) {
        var pacienteFactory = new PatientFactory();
        var patient = new Paciente(
                "12345678909",
                "123456789",
                "Fabiano Shidaka Maeda",
                Sexo.MASCULINO,
                RacaCor.AMARELA,
                IdentidadeGenero.MASCULINO,
                new Paciente.Endereco(
                        "Rua Paris", "11", "", "Centro", "87935-180", "PR", "Paranavaí", "Brasil"
                ),
                List.of(new Paciente.Endereco(
                        "Av. Brasil", "1", "Casa", "Centro", "87800-000", "PR", "Rondon", "Brasil"
                )),
                List.of(new Paciente.Contato(
                                "Maria da Silva",
                                "(61) 5555-8888",
                                "(61) 98888-8888",
                                "teste@teste.com",
                                new Paciente.Endereco("Av. São Paulo", "123", "Ap 1001", "Vila ABC", "85000-000", "PR", "Maringá", "Brasil")
                        )
                ));

        RacBundleFactory factory = new RacBundleFactory();
        Bundle racBundle = factory.createRacBundle(patient);

        IParser jsonParser = FhirContext.forR4().newJsonParser();
        String jsonOutput = jsonParser.setPrettyPrint(true).encodeResourceToString(racBundle);
        System.out.println(jsonOutput);

    }
//        Composition composition = new Composition();
//        composition.setId("7681618d-399d-4291-b321-32ce6d645088");
//        composition.setStatus(Composition.CompositionStatus.FINAL);
//        composition.setTitle("Registro de Atendimento Clínico");
//
//        CodeableConcept type = new CodeableConcept();
//        type.addCoding(new Coding()
//                .setSystem("http://loinc.org")
//                .setCode("11488-4")
//        );
//        type.setText("Nota de consulta");
//        composition.setType(type);
//
//        CodeableConcept category = new CodeableConcept();
//        category.addCoding(new Coding()
//                .setSystem("http://loinc.org")
//                .setCode("11488-4")
//        );
//        category.setText("Nota de consulta");
//        composition.addCategory(category);
//
//        composition.setSubject(new Reference()
//                .setReference("Patient/b2f63a4c-ddaa-4652-8481-ac1ff473104e")
//        );
//
//        composition.setEncounter(new Reference()
//                .setReference("Encounter/c6d553d8-cce0-404b-9d1f-eeffbf7fc6df")
//        );
//
//        composition.setDate(new Date());
//
//        composition.addAuthor(new Reference()
//                .setReference("Practitioner/d3b61455-f432-48f0-b83b-ba825922d735")
//        );
//
//        addSection(composition, "Diagnósticos Avaliados",
//                new Coding("http://loinc.org", "57852-6", "Problem list Reported"),
//                "Lista de Problemas - Diagnósticos Avaliados",
//                new Reference("Condition/91d8d24f-a70d-4ebf-8dea-71cf92ce1bd5"));
//
//        addSection(composition, "Procedimentos Realizados",
//                new Coding("http://loinc.org", "47519-4", "Procedures Hx Doc"),
//                "Procedimentos Realizados",
//                new Reference("Procedure/b0030c23-bd7d-445e-bfe5-020828f7dbaf"));
//
//        addSection(composition, "Sinais Vitais",
//                new Coding("http://loinc.org", "85354-9", "Blood pressure panel with all children optional"),
//                null,
//                new Reference("Observation/07ce5462-f77b-4ad6-af8c-2f67a6500b97"));
//
//        addSection(composition, "Alergias e Intolerâncias",
//                new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRAlergenosCBARA", "A100", "Amendoim"),
//                null,
//                new Reference("AllergyIntolerance/42a8e4ee-cc8b-4c07-bee2-da8fe14ada34"));
//
//        addSection(composition, "Medicamentos",
//                new Coding("http://www.whocc.no/atc", "J01MA12", "levofloxacin"),
//                null,
//                new Reference("Medication/3a46210c-7eb3-48ec-aa67-db7e7ee18864"));
//
//        addSection(composition, "Plano de Cuidados",
//                new Coding("http://loinc.org", "18776-5", "Plan of care note"),
//                null,
//                new Reference("CarePlan/1ed343f2-5231-40d6-a6ea-4428105b7532"));
//
//        System.out.println("Composition FHIR criada com sucesso!");
//
//        IParser jsonParser = new FhirContext().newJsonParser();
//        String jsonOutput = jsonParser.setPrettyPrint(true).encodeResourceToString(composition);
//        System.out.println(jsonOutput);
//    }
//
//    private static void addSection(Composition composition,
//                                   String title,
//                                   Coding coding,
//                                   String text,
//                                   Reference entryReference) {
//        Composition.SectionComponent section = new Composition.SectionComponent();
//        section.setTitle(title);
//
//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(coding);
//        if (text != null) {
//            code.setText(text);
//        }
//        section.setCode(code);
//
//        section.addEntry(entryReference);
//        composition.addSection(section);
}
