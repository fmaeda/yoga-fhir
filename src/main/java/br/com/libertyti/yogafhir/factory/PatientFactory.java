package br.com.libertyti.yogafhir.factory;

import br.com.libertyti.yogafhir.model.Sexo;
import br.com.libertyti.yogafhir.model.IdentidadeGenero;
import br.com.libertyti.yogafhir.model.Paciente;
import br.com.libertyti.yogafhir.model.RacaCor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.V3RoleCode;

import java.util.List;

@NoArgsConstructor
public class PatientFactory {

    public Patient createPatient(Paciente paciente) {
        Patient patient = new Patient();
        patient.addIdentifier(buildCpf(paciente));
        patient.addIdentifier(buildCns(paciente));

        patient.addName(createHumanName(paciente.nome()));
        buildEnderecos(patient, paciente.enderecos());
        buildContatos(patient, paciente.contatos());

        patient.addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/patient-birthPlace", buildEndereco(paciente.enderecoNascimento())));
        patient.addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/patient-genderIdentity", buildIdentidadeGenero(paciente.genero())));
        patient.addExtension(new Extension("https://ips.saude.gov.br/fhir/StructureDefinition/raca-br-ips", buildRacaCor(paciente.raca())));
        patient.addExtension(new Extension("https://ips.saude.gov.br/fhir/StructureDefinition/sexo-nascimento-br-ips", buildSexo(paciente.sexo())));
        return patient;
    }

    private Identifier buildCns(Paciente paciente) {
        return new Identifier()
                .setUse(Identifier.IdentifierUse.OFFICIAL)
                .setSystem("https://saude.gov.br/fhir/sid/cns")
                .setValue(paciente.cns())
                .setType(
                        new CodeableConcept()
                                .addCoding(
                                        new Coding()
                                                .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                                .setCode("HC")
                                                .setDisplay("Health Card Number")
                                )
                );
    }

    private Identifier buildCpf(Paciente paciente) {
        return new Identifier()
                .setUse(Identifier.IdentifierUse.OFFICIAL)
                .setSystem("https://saude.gov.br/fhir/sid/cpf")
                .setValue(paciente.cpf())
                .setType(
                        new CodeableConcept()
                                .addCoding(
                                        new Coding()
                                                .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                                .setCode("TAX")
                                                .setDisplay("Tax ID number")
                                )
                );
    }

    private CodeableConcept buildSexo(Sexo sexo) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://hl7.org/fhir/administrative-gender")
                        .setCode(sexo.getCode())
                        .setDisplay(sexo.getDescription()));
    }

    private CodeableConcept buildRacaCor(RacaCor raca) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("https://terminologia.saude.gov.br/fhir/CodeSystem/BRRacaCor")
                        .setCode(raca.getCode())
                        .setDisplay(raca.getDescription()));
    }

    private HumanName createHumanName(String name) {
        var splittedName = StringUtils.split(name, " ");
        var humanName = new HumanName()
                .setFamily(splittedName[splittedName.length - 1]);

        if (splittedName.length > 1) {
            for (int i = 0; i < splittedName.length - 2; i++) {
                humanName.addGiven(splittedName[i]);
            }
        }
        return humanName;
    }

    private CodeableConcept buildIdentidadeGenero(IdentidadeGenero genero) {
        return new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode(genero.getLoinc())
                        .setDisplay(genero.getDescription()));
    }

    private void buildEnderecos(Patient patient, List<Paciente.Endereco> enderecos) {
        enderecos.forEach(endereco -> {
            var address = buildEndereco(endereco);
            patient.addAddress(address);
        });
    }

    private Address buildEndereco(Paciente.Endereco endereco) {
        return new Address().setUse(Address.AddressUse.HOME)
                .setType(Address.AddressType.BOTH)
                .setText(endereco.logradouro())
                .addLine(endereco.logradouro() + endereco.numero())
                .setCity(endereco.cidade())
                .setState(endereco.uf())
                .setPostalCode(endereco.cep())
                .setCountry(endereco.pais());
    }

    private void buildContatos(Patient patient, List<Paciente.Contato> contatos) {
        contatos.forEach(contato -> {
            var contact = buildContato(contato);
            patient.addContact(contact);
        });
    }

    private Patient.ContactComponent buildContato(Paciente.Contato contato) {
        var contact = new Patient.ContactComponent();
//        contact.addRelationship( //TODO
//                        new CodeableConcept()
//                                .addCoding(
//                                        new Coding()
//                                                .setSystem("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
//                                                .setCode(V3RoleCode.MTH.name())
//                                                .setDisplay("Mother")
//                                )
//                )
//                .setName(createHumanName(contato.nome()))
//                .setGender(Enumerations.AdministrativeGender.FEMALE);

        if (StringUtils.isNotBlank(contato.celular())) {
            contact.addTelecom(
                    new ContactPoint()
                            .setSystem(ContactPoint.ContactPointSystem.PHONE)
                            .setValue(contato.celular())
                            .setUse(ContactPoint.ContactPointUse.MOBILE));
        }

        if (StringUtils.isNotBlank(contato.telefone())) {
            contact.addTelecom(
                    new ContactPoint()
                            .setSystem(ContactPoint.ContactPointSystem.PHONE)
                            .setValue(contato.telefone())
                            .setUse(ContactPoint.ContactPointUse.HOME));
        }

        if (StringUtils.isNotBlank(contato.email())) {
            contact.addTelecom(
                    new ContactPoint()
                            .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                            .setValue(contato.email())
                            .setUse(ContactPoint.ContactPointUse.HOME));
        }

        if (contato.endereco() != null) {
            contact.setAddress(buildEndereco(contato.endereco()));
        }
        return contact;
    }

}
