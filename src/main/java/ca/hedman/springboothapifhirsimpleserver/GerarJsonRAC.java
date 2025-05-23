package ca.hedman.springboothapifhirsimpleserver;

import br.com.libertyti.yogafhir.factory.RacBundleFactory;
import br.com.libertyti.yogafhir.model.*;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.hl7.fhir.r4.model.Bundle;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class GerarJsonRAC {

    @SneakyThrows
    public static void main(String[] args) {
        FhirContext fhirContext = FhirContext.forR4();

        var paciente = new Paciente(
                "patient/1",
                "12345678909",
                "123456789",
                "Fabiano Shidaka Maeda",
                Sexo.MASCULINO,
                RacaCor.AMARELA,
                IdentidadeGenero.MASCULINO,
                new Paciente.Endereco(
                        "", "", "", "", "", "PR", "Paranavaí", "Brasil"
                ),
                List.of(new Paciente.Endereco(
                        "Av. Brasil", "1", "Casa", "Centro", "87800-000", "PR", "Rondon", "Brasil"
                )),
                List.of(new Paciente.Contato(
                                "Maria da Silva",
                                "",
                                "",
                                "",
                                new Paciente.Endereco("Av. São Paulo", "123", "Ap 1001", "Vila ABC", "85000-000", "PR", "Maringá", "Brasil")
                        )
                ));

        var profissional = new Profissional("practitioner/1", "Robledo", "91092901209", new ValueObject("225125", "ENFERMEIRO"), new Profissional.Conselho("123456", "CRM", "SP"));
        var cid = new ValueObject("R06.0", "Dispnéia");
        var evidencia = new RegistroAtendimentoClinico.Condicao.Evidencia(List.of(cid), "observacaoclinica/207790-2256");
        var informacoesAdicionais = new RegistroAtendimentoClinico.Condicao.InformacoesAdicionais(profissional.id(), "Texto Adicional", LocalDateTime.now());
        var condicao = new RegistroAtendimentoClinico.Condicao("condition/1", new RegistroAtendimentoClinico.CID("B34.2", "Infecção por coronavírus de localização não especificada", ""), new ValueObject("", ""),
                new ValueObject("", ""), List.of(new ValueObject("01", "Principal")),
                new ValueObject("6736007", "Moderate (severity modifier) (qualifier value)"), List.of(new ValueObject("", ""))
                , "2024-12-19", LocalDateTime.now(), paciente.id(), profissional.id(),
                List.of(evidencia),
                informacoesAdicionais);
        var localDeAtendimento = new RegistroAtendimentoClinico.Local("location/1", "Local de Atendimento");
        var organizacao = new RegistroAtendimentoClinico.Organizacao("oganization/1", "Hospital Sírio-Libanês", "2079127");
        var diagnostico = new RegistroAtendimentoClinico.Atendimento.Diagnostico(condicao,
                new ValueObject("", ""), 1);
        var participante = new RegistroAtendimentoClinico.Atendimento.Participante(List.of(new ValueObject("", "")), profissional.id());
        var atendimento = new RegistroAtendimentoClinico.Atendimento("encounter/1", "final", "2024-04-05T10:00:00Z", "2024-04-05T11:00:00Z", new ValueObject("AMB", "Ambulatory"), new ValueObject("R", "Routine"),
                List.of(new ValueObject("02", "AMBULATORIAL")), new ValueObject("", ""),
                paciente.id(), List.of(participante),
                new RegistroAtendimentoClinico.Periodo(LocalDateTime.now(), LocalDateTime.now().plusHours(1)),
                List.of(new RegistroAtendimentoClinico.CID("B34.2", "Infecção por coronavírus de localização não especificada", "")), List.of(diagnostico),
                localDeAtendimento, organizacao, List.of(new ValueObject("0211060127", "MAPEAMENTO DE RETINA")), List.of(new RegistroAtendimentoClinico.Atendimento.Alergia("Alergia", new ValueObject("A100", "Amendoim"), "Urticária", "Alta", "Alta", LocalDateTime.now(), new ValueObject("", ""), new ValueObject("", ""))));
        var registroAtendimentoClinico = new RegistroAtendimentoClinico(
                "1",
                "final",
                "Registro de Atendimento Clínico",
                LocalDateTime.now(),
                new ValueObject("11488-4", "Consult note"),
                List.of(new ValueObject("11488-4", "Consult note")),
                paciente,
                atendimento,
                profissional
        );
        System.out.println(new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(registroAtendimentoClinico));

        RacBundleFactory factory = new RacBundleFactory(fhirContext);
        Bundle racBundle = factory.createRacBundle(registroAtendimentoClinico);

        IParser jsonParser = fhirContext.newJsonParser();
        String jsonOutput = jsonParser.setPrettyPrint(true).encodeResourceToString(racBundle);
        try (FileWriter file = new FileWriter("src/main/resources/rac-output.json")) {
            file.write(jsonOutput);
            System.out.println("JSON output written to rac-output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
//        Composition composition = new Composition();
//        composition.setId("7681618d-399d-4291-b321-32ce6d645088");
//        composition.setStatus(Composition.CompositionStatus.FINAL);
//        composition.setTitle("Registro de Atendimento Clínico");
//
//        CodeableConcept type = new CodeableConcept();
//        type.addCoding(new Coding()
//                .setSystem("https://loinc.org/")
//                .setCode("11488-4")
//        );
//        type.setText("Nota de consulta");
//        composition.setType(type);
//
//        CodeableConcept category = new CodeableConcept();
//        category.addCoding(new Coding()
//                .setSystem("https://loinc.org/")
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
//                new Coding("https://loinc.org/", "57852-6", "Problem list Reported"),
//                "Lista de Problemas - Diagnósticos Avaliados",
//                new Reference("Condition/91d8d24f-a70d-4ebf-8dea-71cf92ce1bd5"));
//
//        addSection(composition, "Procedimentos Realizados",
//                new Coding("https://loinc.org/", "47519-4", "Procedures Hx Doc"),
//                "Procedimentos Realizados",
//                new Reference("Procedure/b0030c23-bd7d-445e-bfe5-020828f7dbaf"));
//
//        addSection(composition, "Sinais Vitais",
//                new Coding("https://loinc.org/", "85354-9", "Blood pressure panel with all children optional"),
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
//                new Coding("https://loinc.org/", "18776-5", "Plan of care note"),
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
