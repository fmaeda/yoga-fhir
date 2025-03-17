package br.com.libertyti.yogafhir.factory;

import br.com.libertyti.yogafhir.model.Paciente;
import org.hl7.fhir.r4.model.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RacBundleFactory {
    private final PatientFactory patientFactory;

    public RacBundleFactory() {
        this.patientFactory = new PatientFactory();
    }

    public Bundle createRacBundle(Paciente paciente) {
        Patient patient = patientFactory.createPatient(paciente);

        Bundle bundle = new Bundle();
        bundle.setId(UUID.randomUUID().toString());
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setTimestamp(new Date());
        bundle.setIdentifier(new Identifier().setValue(UUID.randomUUID().toString()).setSystem("urn:ietf:rfc:3986"));

        // Criar Composition
        Composition composition = createComposition(patient);
        bundle.addEntry(createBundleEntry(composition));

        // Adicionar paciente
        bundle.addEntry(createBundleEntry(patient));

        // Criar e adicionar Encounter
        Encounter encounter = createEncounter(patient);
        bundle.addEntry(createBundleEntry(encounter));

        // Criar e adicionar Practitioner
        Practitioner practitioner = createPractitioner();
        bundle.addEntry(createBundleEntry(practitioner));

        // Criar e adicionar Condition
        Condition condition = createCondition(patient, encounter);
        bundle.addEntry(createBundleEntry(condition));

        // Criar e adicionar Procedure
        Procedure procedure = createProcedure(patient, encounter, practitioner);
        bundle.addEntry(createBundleEntry(procedure));

        // Criar e adicionar Observation (Sinais Vitais)
        Observation observation = createObservation(patient, encounter);
        bundle.addEntry(createBundleEntry(observation));

        // Criar e adicionar AllergyIntolerance
        AllergyIntolerance allergyIntolerance = createAllergyIntolerance(patient);
        bundle.addEntry(createBundleEntry(allergyIntolerance));

        // Criar e adicionar Medication
        Medication medication = createMedication();
        bundle.addEntry(createBundleEntry(medication));

        // Criar e adicionar CarePlan
        CarePlan carePlan = createCarePlan(patient);
        bundle.addEntry(createBundleEntry(carePlan));

        // Criar e adicionar Organization
        Organization organization = createOrganization();
        bundle.addEntry(createBundleEntry(organization));

        // Criar e adicionar Location
        Location location = createLocation(organization);
        bundle.addEntry(createBundleEntry(location));

        // Criar e adicionar DiagnosticReport
        DiagnosticReport diagnosticReport = createDiagnosticReport(patient, encounter, organization);
        bundle.addEntry(createBundleEntry(diagnosticReport));

        return bundle;
    }

    private Composition createComposition(Patient patient) {
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        composition.setStatus(Composition.CompositionStatus.FINAL);
        composition.setTitle("Registro de Atendimento Clínico");
        composition.setDateElement(new DateTimeType("2024-12-19T14:30:00Z"));

        composition.setType(new CodeableConcept().addCoding(
                new Coding("http://loinc.org", "11488-4", "Consult note"))
        );
        composition.setSubject(new Reference("Patient/" + patient.getId()));

        return composition;
    }

    private Encounter createEncounter(Patient patient) {
        Encounter encounter = new Encounter();
        encounter.setId(UUID.randomUUID().toString());
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        encounter.setSubject(new Reference("Patient/" + patient.getId()));
        return encounter;
    }

    private Practitioner createPractitioner() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId(UUID.randomUUID().toString());
        practitioner.addName(new HumanName().setFamily("Oliveira").addGiven("Maria Clara"));
        return practitioner;
    }

    private Condition createCondition(Patient patient, Encounter encounter) {
        Condition condition = new Condition();
        condition.setId(UUID.randomUUID().toString());
        condition.setSubject(new Reference("Patient/" + patient.getId()));
        condition.setEncounter(new Reference("Encounter/" + encounter.getId()));
        condition.setCode(new CodeableConcept().addCoding(
                new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", "R06.0", "Dispnéia"))
        );
        return condition;
    }

    private Procedure createProcedure(Patient patient, Encounter encounter, Practitioner practitioner) {
        Procedure procedure = new Procedure();
        procedure.setId(UUID.randomUUID().toString());
        procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);
        procedure.setSubject(new Reference("Patient/" + patient.getId()));
        procedure.setEncounter(new Reference("Encounter/" + encounter.getId()));
        procedure.addPerformer(new Procedure.ProcedurePerformerComponent()
                .setActor(new Reference("Practitioner/" + practitioner.getId())));
        return procedure;
    }

    private Observation createObservation(Patient patient, Encounter encounter) {
        Observation observation = new Observation();
        observation.setId(UUID.randomUUID().toString());
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.setSubject(new Reference("Patient/" + patient.getId()));
        observation.setEncounter(new Reference("Encounter/" + encounter.getId()));
        observation.setCode(new CodeableConcept().addCoding(
                new Coding("http://loinc.org", "85354-9", "Vital signs"))
        );
        return observation;
    }

    private AllergyIntolerance createAllergyIntolerance(Patient patient) {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.setId(UUID.randomUUID().toString());
        allergyIntolerance.setClinicalStatus(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical", "active", "Active")));
        allergyIntolerance.setVerificationStatus(new CodeableConcept().addCoding(
                new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification", "confirmed", "Confirmed")));
        allergyIntolerance.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);
        allergyIntolerance.setCategory(List.of(new Enumeration<>(new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory(), AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION)));        allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.HIGH);
        allergyIntolerance.setCode(new CodeableConcept().addCoding(
                new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRAlergenosCBARA", "camarao", "Alergia a camarão")));
        allergyIntolerance.setPatient(new Reference("Patient/" + patient.getId()));
        return allergyIntolerance;
    }

    private Medication createMedication() {
        Medication medication = new Medication();
        medication.setId(UUID.randomUUID().toString());
        medication.setCode(new CodeableConcept().addCoding(
                new Coding("http://www.whocc.no/atc", "J01MA12", "Levofloxacino; sistêmico")));
        medication.setStatus(Medication.MedicationStatus.ACTIVE);
        return medication;
    }

    private CarePlan createCarePlan(Patient patient) {
        CarePlan carePlan = new CarePlan();
        carePlan.setId(UUID.randomUUID().toString());
        carePlan.setStatus(CarePlan.CarePlanStatus.ACTIVE);
        carePlan.setIntent(CarePlan.CarePlanIntent.PLAN);
        carePlan.setTitle("Plano de Cuidados para Asma Persistente Moderada");
        carePlan.setDescription("Plano de cuidado voltado para controle de asma e monitoramento do paciente.");
        carePlan.setSubject(new Reference("Patient/" + patient.getId()));
        carePlan.setCreatedElement(new DateTimeType("2024-12-19T00:00:00"));
        return carePlan;
    }

    private Organization createOrganization() {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID().toString());
        organization.setName("Hospital Sírio-Libanês");
        organization.setActive(true);
        return organization;
    }

    private Location createLocation(Organization organization) {
        Location location = new Location();
        location.setId(UUID.randomUUID().toString());
        location.setName("Clínica Geral - Enfermaria Norte");
        location.setManagingOrganization(new Reference("Organization/" + organization.getId()));
        return location;
    }

    private DiagnosticReport createDiagnosticReport(Patient patient, Encounter encounter, Organization organization) {
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(UUID.randomUUID().toString());
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().addCoding(
                new Coding("http://loinc.org", "24323-8", "Complete Blood Count")));
        diagnosticReport.setSubject(new Reference("Patient/" + patient.getId()));
        diagnosticReport.setEncounter(new Reference("Encounter/" + encounter.getId()));
        diagnosticReport.setPerformer(List.of(new Reference("Organization/" + organization.getId())));
        return diagnosticReport;
    }

    private Bundle.BundleEntryComponent createBundleEntry(Resource resource) {
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setFullUrl("urn:uuid:" + resource.getId());
        entry.setResource(resource);
        return entry;
    }
}