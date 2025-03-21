package br.com.libertyti.yogafhir.factory;

import br.com.libertyti.yogafhir.codegen.BRCorePatient;
import br.com.libertyti.yogafhir.model.Paciente;
import ca.uhn.fhir.context.FhirContext;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.context.IWorkerContext;
import org.hl7.fhir.r4.context.SimpleWorkerContext;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.util.List;

public class PatientFactory {

    private final IWorkerContext workerContext;

    public PatientFactory(FhirContext fhirContext) {
        this.workerContext = this.initWorkerContext(fhirContext);
    }

    public Patient createPatient(Paciente paciente) {
        BRCorePatient patient = new BRCorePatient();
        patient.setId(paciente.id());
        patient.setCpf(paciente.cpf());
        patient.setCns(paciente.cns());
        patient.setName(paciente.nome());

        buildEnderecos(patient, paciente.enderecos());
        buildContatos(patient, paciente.contatos());

        patient.setLocalNascimento(buildEndereco(paciente.enderecoNascimento()));
        patient.setSexoNascimento(paciente.sexo());
        patient.setIdentidadeGenero(paciente.genero());
        patient.setRaca(paciente.raca());
        return patient.build(workerContext);
    }

    private void buildEnderecos(BRCorePatient patient, List<Paciente.Endereco> enderecos) {
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

    private void buildContatos(BRCorePatient patient, List<Paciente.Contato> contatos) {
        contatos.forEach(contato -> {
            var contact = buildContato(contato);
            patient.getContacts().add(contact);
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

    @SneakyThrows
    private IWorkerContext initWorkerContext(FhirContext fhirContext) {
        FilesystemPackageCacheManager packageManager = new FilesystemPackageCacheManager.Builder().withTestingCacheFolder().build();

        var wContext = SimpleWorkerContext.fromNothing();
        wContext.loadFromPackage(packageManager.loadPackage("hl7.fhir.r4.core", "4.0.1"), null);
        wContext.loadFromPackage(NpmPackage.fromPackage(new FileInputStream(ResourceUtils.getFile("classpath:definitions/brcore.tgz"))), null);
        wContext.loadFromPackage(NpmPackage.fromPackage(new FileInputStream(ResourceUtils.getFile("classpath:definitions/ips.tgz"))), null);
        wContext.loadFromPackage(NpmPackage.fromPackage(new FileInputStream(ResourceUtils.getFile("classpath:definitions/terminologias.tgz"))), null);
        wContext.loadFromPackage(NpmPackage.fromPackage(new FileInputStream(ResourceUtils.getFile("classpath:definitions/ans.tgz"))), null);
        wContext.loadFromPackage(NpmPackage.fromPackage(new FileInputStream(ResourceUtils.getFile("classpath:definitions/rnds.tgz"))), null);
        return wContext;
    }
}
