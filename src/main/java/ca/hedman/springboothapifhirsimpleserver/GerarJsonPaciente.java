package ca.hedman.springboothapifhirsimpleserver;

import br.com.libertyti.yogafhir.factory.PatientFactory;
import br.com.libertyti.yogafhir.model.IdentidadeGenero;
import br.com.libertyti.yogafhir.model.Paciente;
import br.com.libertyti.yogafhir.model.RacaCor;
import br.com.libertyti.yogafhir.model.Sexo;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.V3RoleCode;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GerarJsonPaciente {

    private static final String URN_LIBERTY = "urn:liberty:";

    public static void main(String[] args) {
        FhirContext ctx = FhirContext.forR4();
        var pacienteFactory = new PatientFactory(ctx);

        Patient patient = pacienteFactory.createPatient(new Paciente(
                "207790-6543",
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
                ))
        ));

        var parser = ctx.newJsonParser();
        String encoded = parser.setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println("Encoded: " + encoded);
    }

}
