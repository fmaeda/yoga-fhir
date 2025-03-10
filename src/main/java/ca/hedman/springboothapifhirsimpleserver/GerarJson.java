package ca.hedman.springboothapifhirsimpleserver;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import java.util.Date;

public class GerarJson {

    public static void main(String[] args) {
        Patient patient = new Patient();
        patient.setId("b1997888-85fd-41b6-bf94-6242cf3c8265");

        patient.addIdentifier()
                .setUse(Identifier.IdentifierUse.OFFICIAL)
                .setSystem("https://saude.gov.br/fhir/sid/cpf")
                .setValue("34567890123")
                .setType(
                        new CodeableConcept()
                                .addCoding(
                                        new Coding()
                                                .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                                .setCode("TAX")
                                                .setDisplay("Tax ID number")
                                )
                );
        patient.addIdentifier()
                .setUse(Identifier.IdentifierUse.OFFICIAL)
                .setSystem("https://saude.gov.br/fhir/sid/cns")
                .setValue("765432109876543")
                .setType(
                        new CodeableConcept()
                                .addCoding(
                                        new Coding()
                                                .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
                                                .setCode("HC")
                                                .setDisplay("Health Card Number")
                                )
                );

        patient.addName()
                .setUse(HumanName.NameUse.OFFICIAL)
                .setFamily("Oliveira")
                .addGiven("Ana")
                .addGiven("Beatriz");
        patient.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setUse(ContactPoint.ContactPointUse.MOBILE)
                .setValue("+55 31 99876-5432");
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        patient.setBirthDate(new Date());

        patient.addAddress()
                .setUse(Address.AddressUse.HOME)
                .setType(Address.AddressType.BOTH)
                .setText("Rua da Bahia, 300, Centro, Belo Horizonte, MG, 30160-010")
                .addLine("Rua da Bahia, 300")
                .setCity("Belo Horizonte")
                .setState("MG")
                .setPostalCode("30160-010")
                .setCountry("Brasil");

        patient.addContact()
                .addRelationship(
                        new CodeableConcept()
                                .addCoding(
                                        new Coding()
                                                .setSystem("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                                                .setCode("MTH")
                                                .setDisplay("Mother")
                                )
                )
                .setName(
                        new HumanName()
                                .setUse(HumanName.NameUse.OFFICIAL)
                                .setFamily("Oliveira")
                                .addGiven("Juliana")
                                .addGiven("Cristina")
                )
                .addTelecom(
                        new ContactPoint()
                                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                                .setValue("+55 31 99765-4321")
                                .setUse(ContactPoint.ContactPointUse.MOBILE)

                )
                .addTelecom(
                        new ContactPoint()
                                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                                .setValue("juliana.oliveira@example.com")
                                .setUse(ContactPoint.ContactPointUse.HOME)
                )
                .setAddress(
                        new Address()
                                .setUse(Address.AddressUse.HOME)
                                .setText("Rua da Bahia, 300, Centro, Belo Horizonte, MG, 30160-010")
                                .addLine("Rua da Bahia, 300")
                                .setCity("Belo Horizonte")
                                .setState("MG")
                                .setPostalCode("30160-010")
                                .setCountry("Brasil")
                )
                .setGender(Enumerations.AdministrativeGender.FEMALE);

        var birthPlaceExt = new Extension("http://hl7.org/fhir/StructureDefinition/patient-birthPlace", new Address()
                .setCity("Belo Horizonte")
                .setState("MG")
                .setCountry("Brasil"));

//        var birthPlaceExt = new Extension()
//                .setUrl("http://hl7.org/fhir/StructureDefinition/patient-birthPlace");
//        birthPlaceExt.setProperty("valueAddress", new Address()
//                .setCity("Belo Horizonte")
//                .setState("MG")
//                .setCountry("Brasil"));
        patient.addExtension(birthPlaceExt);

        var genderExt = new Extension("http://hl7.org/fhir/StructureDefinition/patient-genderIdentity", new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://snomed.info/sct")
                        .setCode("446141000124107")
                        .setDisplay("Identifies as female gender (finding)")
                )
        );
        patient.addExtension(genderExt);

        var racaExt = new Extension("https://ips.saude.gov.br/fhir/StructureDefinition/raca-br-ips", new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("https://terminologia.saude.gov.br/fhir/CodeSystem/BRRacaCor")
                        .setCode("02")
                        .setDisplay("Preta")
                ));
        patient.addExtension(racaExt);

        var sexoExt = new Extension("https://ips.saude.gov.br/fhir/StructureDefinition/sexo-nascimento-br-ips", new CodeableConcept()
                .addCoding(new Coding()
                        .setSystem("http://hl7.org/fhir/administrative-gender")
                        .setCode("female")
                        .setDisplay("Female")
                ));
        patient.addExtension(sexoExt);

        FhirContext ctx = FhirContext.forR4();
        var parser = ctx.newJsonParser();
        String encoded = parser.setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println("Encoded: " + encoded);
    }

}
