package br.com.libertyti.yogafhir.factory;

import br.com.libertyti.yogafhir.model.Paciente;
import br.com.libertyti.yogafhir.model.RegistroAtendimentoClinico;
import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static br.com.libertyti.yogafhir.codegen.BRCorePatient.createHumanName;

public class RacBundleFactory {
    private final PatientFactory patientFactory;

    public RacBundleFactory(FhirContext ctx) {
        this.patientFactory = new PatientFactory(ctx);
    }

//    public Bundle createRacBundle(Paciente paciente) {
//        Patient patient = patientFactory.createPatient(paciente);
//
//        Bundle bundle = new Bundle();
//        bundle.setType(Bundle.BundleType.DOCUMENT);
//        bundle.setId(UUID.randomUUID().toString());
//        bundle.setTimestamp(new Date());
//        bundle.setIdentifier(new Identifier().setValue(UUID.randomUUID().toString()).setSystem("urn:ietf:rfc:3986"));
//
//        Composition composition = createComposition(patient);
//        bundle.addEntry(createBundleEntry(composition));
//
//        bundle.addEntry(createBundleEntry(patient));
//
//        Organization organization = createOrganization();
//        bundle.addEntry(createBundleEntry(organization));
//
//        Practitioner practitioner = createPractitioner(organization);
//        bundle.addEntry(createBundleEntry(practitioner));
//
//        Location location = createLocation(organization);
//        bundle.addEntry(createBundleEntry(location));
//
//        Encounter encounter = createEncounter(patient, organization, location, practitioner);
//        bundle.addEntry(createBundleEntry(encounter));
//
//        Condition condition = createCondition(patient, encounter, practitioner);
//        bundle.addEntry(createBundleEntry(condition));
//
//        Procedure procedure = createProcedure(patient, encounter, practitioner);
//        bundle.addEntry(createBundleEntry(procedure));
//
//        Observation observation = createObservation(patient, encounter);
//        bundle.addEntry(createBundleEntry(observation));
//
//        AllergyIntolerance allergyIntolerance = createAllergyIntolerance(patient);
//        bundle.addEntry(createBundleEntry(allergyIntolerance));
//
//        Medication medication = createMedication();
//        bundle.addEntry(createBundleEntry(medication));
//
//        CarePlan carePlan = createCarePlan(patient);
//        bundle.addEntry(createBundleEntry(carePlan));
//
//        DiagnosticReport diagnosticReport = createDiagnosticReport(patient, encounter, organization);
//        bundle.addEntry(createBundleEntry(diagnosticReport));
//
//        return bundle;
//    }

    public Bundle createRacBundle(RegistroAtendimentoClinico rac) {
        Paciente paciente = rac.paciente();
        Patient patient = patientFactory.createPatient(paciente);

        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.DOCUMENT);
        bundle.setId(UUID.randomUUID().toString());
        bundle.setTimestamp(new Date());
        bundle.setIdentifier(new Identifier().setValue(UUID.randomUUID().toString()).setSystem("urn:ietf:rfc:3986"));

        var organization = createOrganization(rac);
        var practitioner = createPractitioner(rac);
        var location = createLocation(rac, organization);
        var encounter = createEncounter(rac, patient, organization, location);
        var condition = createCondition(rac, patient, encounter);
        var procedure = createProcedure(rac, patient, encounter, practitioner);
        var allergyIntolerance = createAllergyIntolerance(patient);
//        var medication = createMedication();
//        var carePlan = createCarePlan(patient);
//        var diagnosticReport = createDiagnosticReport(patient, encounter, organization);
        Composition composition = createComposition(rac, procedure, allergyIntolerance);


        bundle.addEntry(createBundleEntry(composition));
        bundle.addEntry(createBundleEntry(patient));
        bundle.addEntry(createBundleEntry(organization));
        bundle.addEntry(createBundleEntry(practitioner));
        bundle.addEntry(createBundleEntry(location));
        bundle.addEntry(createBundleEntry(encounter));
        bundle.addEntry(createBundleEntry(condition));
        bundle.addEntry(createBundleEntry(procedure));
        bundle.addEntry(createBundleEntry(allergyIntolerance));
//        bundle.addEntry(createBundleEntry(medication));
//        bundle.addEntry(createBundleEntry(carePlan));
//        bundle.addEntry(createBundleEntry(diagnosticReport));

        return bundle;
    }

    private Composition createComposition(RegistroAtendimentoClinico rac, Procedure procedure, AllergyIntolerance allergyIntolerance) {
        Composition composition = new Composition();
        composition.setId(UUID.randomUUID().toString());
        composition.setStatus(Composition.CompositionStatus.fromCode(rac.status()));
        composition.setTitle(rac.titulo());
        composition.setDateElement(new DateTimeType(rac.data().atZone(ZoneId.of("America/Sao_Paulo")).toString()));

        CodeableConcept type = new CodeableConcept();
        type.addCoding(new Coding("http://loinc.org", rac.tipo().codigo(), rac.tipo().descricao()));
        type.setText(rac.tipo().descricao());
        composition.setType(type);

        rac.categorias().forEach(c -> {
            CodeableConcept category = new CodeableConcept();
            category.addCoding(new Coding("http://loinc.org", c.codigo(), c.descricao()));
            category.setText(c.descricao());
            composition.addCategory(category);
        });

        composition.setSubject(new Reference("urn:uuid:" + rac.paciente().id()));
        composition.setEncounter(new Reference("urn:uuid:" + rac.atendimento().id()));
        
        composition.addAuthor(new Reference("urn:uuid:" + rac.autor().id()));
        
        addSection(composition, "Diagnósticos Avaliados",
                new Coding("http://loinc.org", "57852-6", "Problem list Reported"),
                "Lista de Problemas - Diagnósticos Avaliados",
                rac.atendimento().diagnosticos().stream().map(d -> new Reference("urn:uuid:" + d.condicao().id())).toList());

        addSection(composition, "Procedimentos Realizados",
                new Coding(procedure.getCode().getCodingFirstRep().getSystem(), procedure.getCode().getCodingFirstRep().getCode(), procedure.getCode().getCodingFirstRep().getDisplay()),
                "Procedimentos Realizados",
                List.of(new Reference("urn:uuid:" + procedure.getId())));

        addSection(composition, "Sinais Vitais",
                null,
                null,
                Collections.emptyList());

        addSection(composition, "Alergias e Intolerâncias",
                null,
                null,
                List.of(new Reference("urn:uuid:"+ allergyIntolerance.getId())));

        addSection(composition, "Medicamentos",
                null,
                null,
                Collections.emptyList());

        addSection(composition, "Plano de Cuidados",
                null,
                null,
                Collections.emptyList());

        return composition;
    }

    private Encounter createEncounter(RegistroAtendimentoClinico rac, Patient patient, Organization organization, Location location) {
        Encounter encounter = new Encounter();
        encounter.setId(UUID.randomUUID().toString());
        
        encounter.addIdentifier(new Identifier().setSystem("https://saude.gov.br/fhir/sid/encounter").setValue("ENC-12345"));
        
        encounter.setStatus(Encounter.EncounterStatus.FINISHED);
        
        Coding classCoding = new Coding("http://terminology.hl7.org/CodeSystem/v3-ActCode", rac.atendimento().ato().codigo(), rac.atendimento().ato().descricao());
        encounter.setClass_(classCoding);
        
        CodeableConcept type = new CodeableConcept();
        rac.atendimento().tipos().forEach(t -> type.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRAtendimentoPrestado", t.codigo(), t.descricao())));
        encounter.addType(type);
        
        CodeableConcept serviceType = new CodeableConcept();
        serviceType.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRServicoEspecializado", rac.atendimento().tipoServico().codigo(), rac.atendimento().tipoServico().descricao()));
        encounter.setServiceType(serviceType);
        
        CodeableConcept priority = new CodeableConcept();
        priority.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v3-ActPriority", rac.atendimento().prioridadeDoAto().codigo(), rac.atendimento().prioridadeDoAto().descricao()));
        encounter.setPriority(priority);
        
        encounter.setSubject(new Reference("urn:uuid:" + patient.getId()));

        rac.atendimento().participantes().forEach(p -> {
            Encounter.EncounterParticipantComponent participant = new Encounter.EncounterParticipantComponent();
            p.tipos().forEach(t -> participant.addType(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v3-ParticipationType", t.codigo(), t.descricao()))));
            participant.setIndividual(new Reference("urn:uuid:" + p.profissionalId()));
            encounter.addParticipant(participant);
        });

        Period period = new Period();
        period.setStartElement(new DateTimeType(rac.atendimento().dataInicio()));
        period.setEndElement(new DateTimeType(rac.atendimento().dataFim()));
        encounter.setPeriod(period);

        rac.atendimento().cids().forEach(cid -> { // fixme acho que aqui seriam só cids relatados no momento de admissão do paciente
            CodeableConcept reasonCode = new CodeableConcept();
            reasonCode.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", cid.codigo(), cid.descricao()));
            encounter.addReasonCode(reasonCode);
        });

        rac.atendimento().diagnosticos().forEach(d -> {
            Encounter.DiagnosisComponent diagnosis = new Encounter.DiagnosisComponent();
            diagnosis.setCondition(new Reference("urn:uuid:" + d.condicao().id()).setDisplay(d.condicao().cid().descricao()));
            CodeableConcept diagnosisUse = new CodeableConcept();
            diagnosisUse.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/diagnosis-role", d.tipo().codigo(), d.tipo().descricao()));
            diagnosis.setUse(diagnosisUse);
            diagnosis.setRank(d.classificacao());
            encounter.addDiagnosis(diagnosis);
        });

        Encounter.EncounterLocationComponent encounterLocation = new Encounter.EncounterLocationComponent();
        encounterLocation.setLocation(new Reference("urn:uuid:" + location.getId()));
        encounterLocation.setStatus(Encounter.EncounterLocationStatus.ACTIVE);
        encounter.addLocation(encounterLocation);
        
        encounter.setServiceProvider(new Reference("urn:uuid:" + organization.getId()));

        return encounter;
    }

    private Practitioner createPractitioner(RegistroAtendimentoClinico rac) {
        Practitioner practitioner = new Practitioner();
        practitioner.setId(UUID.randomUUID().toString());

        Identifier identifier1 = new Identifier();
        identifier1.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier1.setSystem("https://saude.gov.br/fhir/sid/crm-sp");
        identifier1.setValue(rac.autor().conselho().numero());
        identifier1.setType(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "MD", "Medical License number")).setText("Número de Registro no Conselho Federal de Medicina"));
        practitioner.addIdentifier(identifier1);

        Identifier identifier2 = new Identifier();
        identifier2.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier2.setSystem("https://saude.gov.br/fhir/sid/cns");
        identifier2.setValue(rac.autor().cns());
        identifier2.setType(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/v2-0203", "HC", "Health Card Number")).setText("Número do Cartão Nacional de Saúde"));
        practitioner.addIdentifier(identifier2);

        practitioner.setActive(true);
        practitioner.addName(createHumanName(rac.autor().name()));


        Practitioner.PractitionerQualificationComponent qualification = new Practitioner.PractitionerQualificationComponent();
        Identifier qualificationIdentifier = new Identifier();
        qualificationIdentifier.setSystem("https://saude.gov.br/fhir/sid/cns");
        qualificationIdentifier.setValue(rac.autor().cns());
        qualification.addIdentifier(qualificationIdentifier);

        CodeableConcept qualificationCode = new CodeableConcept();
        qualificationCode.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem-BRCBO.html", rac.autor().cbo().codigo(), rac.autor().cbo().descricao()));
        qualification.setCode(qualificationCode);
        practitioner.addQualification(qualification);

        return practitioner;
    }

    // fixme validar relacionamento de cids com atendimento e diagnostico Robledo, no RES mostramos DIAGNÓSTICO/PROBLEMA
    private Condition createCondition(RegistroAtendimentoClinico rac, Patient patient, Encounter encounter) {
        Condition condition = new Condition();
        condition.setId(UUID.randomUUID().toString());

        rac.atendimento().cids().forEach(cid -> {
            CodeableConcept code = new CodeableConcept();
            code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", cid.codigo(), cid.descricao()));
            condition.setCode(code);


        });
//        fixme dados adicionais que não sei se temos
//        CodeableConcept clinicalStatus = new CodeableConcept();
//        clinicalStatus.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", "Active"));
//        condition.setClinicalStatus(clinicalStatus);
//
//        CodeableConcept verificationStatus = new CodeableConcept();
//        verificationStatus.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/condition-ver-status", "confirmed", "Confirmed"));
//        condition.setVerificationStatus(verificationStatus);
//
//        CodeableConcept category = new CodeableConcept();
//        category.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCategoriaDiagnostico", "01", "Principal"));
//        condition.addCategory(category);
//
//        CodeableConcept severity = new CodeableConcept();
//        severity.addCoding(new Coding("http://snomed.info/sct", "6736007", "Moderate (severity modifier) (qualifier value)"));
//        condition.setSeverity(severity);
//
//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", "I15.9", "Hipertensão secundária, não especificada"));
//        condition.setCode(code);
//
//        CodeableConcept bodySite = new CodeableConcept();
//        bodySite.addCoding(new Coding("http://snomed.info/sct", "80891009", "Heart structure (body structure)"));
//        bodySite.setText("Coração");
//        condition.addBodySite(bodySite);

        condition.setSubject(new Reference("urn:uuid:" + patient.getId()));

        condition.setEncounter(new Reference("urn:uuid:" + encounter.getId()));

        condition.setOnset(new DateTimeType(LocalDateTime.now().toString()));

        condition.setRecordedDateElement(new DateTimeType("2024-04-05T10:00:00Z"));

//        condition.setRecorder(new Reference("urn:uuid:" + practitioner.getId())); // fixme quem registrou o diagnóstico

//        condition.setAsserter(new Reference("urn:uuid:" + practitioner.getId())); // fixme quem confirmou o diagnóstico

//        Condition.ConditionEvidenceComponent evidence = new Condition.ConditionEvidenceComponent();
//        CodeableConcept evidenceCode = new CodeableConcept();
//        evidenceCode.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", "R50.9", "Febre não especificada"));
//        evidenceCode.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", "R05", "Tosse"));
//        evidence.addCode(evidenceCode);
//        evidence.addDetail(new Reference("urn:uuid:52ba1c80-5d2c-4faf-8c90-f4363e20d7bb").setDisplay("Vital Signs"));
//        condition.addEvidence(evidence);

//        Annotation note = new Annotation();
//        note.setText("Paciente apresentou melhora significativa após início do tratamento com antibióticos.");
//        condition.addNote(note);

        return condition;
    }

    private Procedure createProcedure(RegistroAtendimentoClinico rac, Patient patient, Encounter encounter, Practitioner practitioner) {
        Procedure procedure = new Procedure();
        procedure.setId(UUID.randomUUID().toString());

        Identifier identifier = new Identifier();
        identifier.setSystem("https://saude.gov.br/fhir/sid/procedure");
        identifier.setValue(rac.atendimento().procedimentos().get(0).codigo());
        procedure.addIdentifier(identifier);

        CodeableConcept code = new CodeableConcept();
        code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRTabelaSUS", rac.atendimento().procedimentos().get(0).codigo(), rac.atendimento().procedimentos().get(0).descricao()));
        procedure.setCode(code);

        procedure.setStatus(Procedure.ProcedureStatus.COMPLETED);

//        CodeableConcept statusReason = new CodeableConcept();
//        statusReason.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/ValueSet/BRMotivoProcedimentoNaoRealizado", "03", "Procedimento não realizado"));
//        procedure.setStatusReason(statusReason);
//
//        CodeableConcept category = new CodeableConcept();
//        category.addCoding(new Coding("https://fhir.ans.gov.br/CodeSystem/tuss-50", "04", "Consulta"));
//        category.setText("Consulta");
//        procedure.setCategory(category);
//
//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRTabelaSUS", "0301010064", "Consulta médica em atenção primária"));
//        code.setText("Consulta médica em atenção primária");
//        procedure.setCode(code);
//
//        procedure.setSubject(new Reference("urn:uuid:" + patient.getId()));
//
//        procedure.setEncounter(new Reference("urn:uuid:" + encounter.getId()));
//
//        Period performedPeriod = new Period();
//        performedPeriod.setStartElement(new DateTimeType("2023-12-12T09:00:00-03:00"));
//        performedPeriod.setEndElement(new DateTimeType("2023-12-12T10:30:00-03:00"));
//        procedure.setPerformed(performedPeriod);
//
//        Procedure.ProcedurePerformerComponent performer = new Procedure.ProcedurePerformerComponent();
//        CodeableConcept performerFunction = new CodeableConcept();
//        performerFunction.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/participant-type", "emergency", "Emergency"));
//        performerFunction.setText("Emergência");
//        performer.setFunction(performerFunction);
//        performer.setActor(new Reference("urn:uuid:" + practitioner.getId()));
//        procedure.addPerformer(performer);
//
//        CodeableConcept reasonCode = new CodeableConcept();
//        reasonCode.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRCID10", "I10", "Hipertensão essencial (primária)"));
//        reasonCode.setText("Hipertensão essencial (primária)");
//        procedure.addReasonCode(reasonCode);
//
//        CodeableConcept bodySite = new CodeableConcept();
//        bodySite.addCoding(new Coding("http://snomed.info/sct", "480000", "Cardiopulmonary circulatory system"));
//        bodySite.setText("Sistema Circulatório");
//        procedure.addBodySite(bodySite);
//
//        CodeableConcept outcome = new CodeableConcept();
//        outcome.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRDesfechoProcedimento", "01", "Melhora Clínica"));
//        outcome.setText("Melhora Clínica");
//        procedure.setOutcome(outcome);
//
//        procedure.addReport(new Reference("DiagnosticReport/example-report"));
//
//        procedure.addComplicationDetail(new Reference("urn:uuid:91d8d24f-a70d-4ebf-8dea-71cf92ce1bd5"));
//
//        CodeableConcept followUp = new CodeableConcept();
//        followUp.setText("Consulta de acompanhamento em 2 semanas");
//        procedure.addFollowUp(followUp);
//
//        Annotation note = new Annotation();
//        note.setText("O paciente foi orientado a fazer atividade física.");
//        procedure.addNote(note);

        return procedure;
    }

    private AllergyIntolerance createAllergyIntolerance(Patient patient) {
        AllergyIntolerance allergyIntolerance = new AllergyIntolerance();
        allergyIntolerance.setId(UUID.randomUUID().toString());
//        fixme dados adicionais que não sei se temos
//        CodeableConcept clinicalStatus = new CodeableConcept();
//        clinicalStatus.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical", "active", "Active"));
//        allergyIntolerance.setClinicalStatus(clinicalStatus);
//
//        CodeableConcept verificationStatus = new CodeableConcept();
//        verificationStatus.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification", "confirmed", "Confirmed"));
//        allergyIntolerance.setVerificationStatus(verificationStatus);

        allergyIntolerance.setType(AllergyIntolerance.AllergyIntoleranceType.ALLERGY);

        allergyIntolerance.setCategory(List.of(new Enumeration<>(new AllergyIntolerance.AllergyIntoleranceCategoryEnumFactory(), AllergyIntolerance.AllergyIntoleranceCategory.MEDICATION)));

        allergyIntolerance.setCriticality(AllergyIntolerance.AllergyIntoleranceCriticality.HIGH);

//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRAlergenosCBARA", "camarao", "Camarão")).setText("Alergia a camarão");
//        allergyIntolerance.setCode(code);

        allergyIntolerance.setPatient(new Reference("urn:liberty:" + patient.getId()));

//        AllergyIntolerance.AllergyIntoleranceReactionComponent reaction = new AllergyIntolerance.AllergyIntoleranceReactionComponent();
//        CodeableConcept manifestation = new CodeableConcept();
//        manifestation.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRMedDRA", "10002424", "angioedema")).setText("Angioedema");
//        reaction.addManifestation(manifestation);
//        reaction.setSeverity(AllergyIntolerance.AllergyIntoleranceSeverity.SEVERE);
//
//        CodeableConcept exposureRoute = new CodeableConcept();
//        exposureRoute.addCoding(new Coding("http://standardterms.edqm.eu", "20045000", "Intravenous use")).setText("Intravenosa");
//        reaction.setExposureRoute(exposureRoute);
//
//        CodeableConcept substance = new CodeableConcept();
//        substance.addCoding(new Coding("http://www.whocc.no/atc", "J01CR50", "combinations of penicillins")).setText("Associações de penicilinas");
//        reaction.setSubstance(substance);
//
//        allergyIntolerance.addReaction(reaction);

        return allergyIntolerance;
    }

//    private Medication createMedication() {
//        Medication medication = new Medication();
//        medication.setId("3a46210c-7eb3-48ec-aa67-db7e7ee18864");
//
//        Identifier identifier = new Identifier();
//        identifier.setSystem("https://saude.gov.br/fhir/sid/medication");
//        identifier.setValue("12345");
//        medication.addIdentifier(identifier);
//
//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(new Coding("http://www.whocc.no/atc", "J01MA12", "levofloxacin"));
//        medication.setCode(code);
//
//        medication.setStatus(Medication.MedicationStatus.ACTIVE);
//
//        medication.setManufacturer(new Reference("urn:uuid:1428c345-c221-411f-880f-6fb163817387"));
//
//        Medication.MedicationIngredientComponent ingredient = new Medication.MedicationIngredientComponent();
//        CodeableConcept ingredientCode = new CodeableConcept();
//        ingredientCode.addCoding(new Coding("http://www.whocc.no/atc", "J01MA12", "levofloxacin"));
//        ingredientCode.setText("Levofloxacino");
//        ingredient.setItem(ingredientCode);
//
//        Ratio strength = new Ratio();
//        Quantity numerator = new Quantity();
//        numerator.setValue(500);
//        numerator.setUnit("mg");
//        numerator.setSystem("urn:oid:2.16.840.1.113883.6.8");
//        numerator.setCode("mg");
//        strength.setNumerator(numerator);
//
//        Quantity denominator = new Quantity();
//        denominator.setValue(1);
//        denominator.setUnit("tablet");
//        denominator.setSystem("urn:oid:2.16.840.1.113883.6.8");
//        denominator.setCode("tablet");
//        strength.setDenominator(denominator);
//
//        ingredient.setStrength(strength);
//        medication.addIngredient(ingredient);
//
//        return medication;
//    }

//    fixme hoje temos apenas para sumario de alta, o desfecho
//    private CarePlan createCarePlan(Patient patient) {
//        CarePlan carePlan = new CarePlan();
//        carePlan.setId("1ed343f2-5231-40d6-a6ea-4428105b7532");
//        carePlan.setInstantiatesUri(List.of(new UriType("http://www.saude.gov.br/plano-cuidados/asma")));
//        carePlan.setStatus(CarePlan.CarePlanStatus.ACTIVE);
//        carePlan.setIntent(CarePlan.CarePlanIntent.PLAN);
//        carePlan.setTitle("Plano de Cuidados para Asma Persistente Moderada");
//        carePlan.setDescription("Plano de cuidado voltado para controle de asma e monitoramento do paciente.");
//        carePlan.setSubject(new Reference("urn:uuid:" + patient.getId()));
//        carePlan.setCreatedElement(new DateTimeType("2024-12-19"));
//        carePlan.setContributor(List.of(new Reference("urn:uuid:1428c345-c221-411f-880f-6fb163817387")));
//        carePlan.setAddresses(List.of(new Reference("urn:uuid:91d8d24f-a70d-4ebf-8dea-71cf92ce1bd5").setDisplay("Dispnéia")));
//
//        CarePlan.CarePlanActivityComponent activity = new CarePlan.CarePlanActivityComponent();
//        CarePlan.CarePlanActivityDetailComponent detail = new CarePlan.CarePlanActivityDetailComponent();
//        detail.setKind(CarePlan.CarePlanActivityKind.SERVICEREQUEST);
//        CodeableConcept code = new CodeableConcept();
//        code.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRSubgrupoTabelaSUS", "0309", "Terapias especializadas"));
//        detail.setCode(code);
//        detail.setStatus(CarePlan.CarePlanActivityStatus.SCHEDULED);
//        Period scheduledPeriod = new Period();
//        scheduledPeriod.setStartElement(new DateTimeType("2024-12-20"));
//        scheduledPeriod.setEndElement(new DateTimeType("2024-12-20"));
//        detail.setScheduled(scheduledPeriod);
//        detail.setPerformer(List.of(new Reference("urn:uuid:1428c345-c221-411f-880f-6fb163817387")));
//        detail.setDescription("Consulta periódica para acompanhamento da asma persistente moderada.");
//        activity.setDetail(detail);
//        carePlan.addActivity(activity);
//
//        return carePlan;
//    }

    private Organization createOrganization(RegistroAtendimentoClinico rac) {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID().toString());
        organization.setIdentifier(List.of(new Identifier().setUse(Identifier.IdentifierUse.OFFICIAL).setSystem("https://saude.gov.br/sid/cnes").setValue("2079127")));
        organization.setType(List.of(new CodeableConcept().addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRTipoEstabelecimentoSaude", "5", "HOSPITAL GERAL"))));
        organization.setName(rac.atendimento().organizacao().descricao());
        organization.setActive(true);
        return organization;
    }

    private Location createLocation(RegistroAtendimentoClinico rac, Organization managingOrganization) {
        Location location = new Location();
        location.setId(UUID.randomUUID().toString());

        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.OFFICIAL);
        identifier.setSystem("https://saude.gov.br/sid/cnes");
        identifier.setValue(rac.atendimento().organizacao().cnes());
        location.addIdentifier(identifier);
        location.setStatus(Location.LocationStatus.ACTIVE);
        location.setName(rac.atendimento().organizacao().descricao());
        location.setMode(Location.LocationMode.INSTANCE);
        CodeableConcept type = new CodeableConcept(); // fixme isso aqui tem que ver com o Robledo, mas talvez apagaremos
        type.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRServicoEspecializado", "148", "HOSPITAL DIA"));
        type.setText("HOSPITAL DIA");
        location.addType(type);

//        CodeableConcept physicalType = new CodeableConcept();
//        physicalType.addCoding(new Coding("https://terminologia.saude.gov.br/fhir/CodeSystem/BRInstalacoesFisicas", "42", "SALA DE ACOLHIMENTO COM CLASSIFICACAO DE RISCO"));
//        location.setPhysicalType(physicalType);

        location.setManagingOrganization(new Reference("urn:uuid:" + managingOrganization.getId()));

        return location;
    }

    private DiagnosticReport createDiagnosticReport(Patient patient, Encounter encounter, Organization organization) {
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(UUID.randomUUID().toString());
        diagnosticReport.setStatus(DiagnosticReport.DiagnosticReportStatus.FINAL);
        diagnosticReport.setCode(new CodeableConcept().addCoding(
                new Coding("http://loinc.org", "24323-8", "Complete Blood Count")));
        diagnosticReport.setSubject(new Reference("urn:uuid:" + patient.getId()));
        diagnosticReport.setEncounter(new Reference("urn:uuid:" + encounter.getId()));
        diagnosticReport.setPerformer(List.of(new Reference("Organization/" + organization.getId())));
        return diagnosticReport;
    }

    private Bundle.BundleEntryComponent createBundleEntry(Resource resource) {
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setFullUrl("urn:uuid:" + resource.getId());
        entry.setResource(resource);
        return entry;
    }

    private void addSection(Composition composition, String title, Coding coding, String text, List<Reference> entries) {
        Composition.SectionComponent section = new Composition.SectionComponent();
        section.setTitle(title);
        if (coding != null) {
            section.getCode().addCoding(coding);
        }
        if (text != null) {
            section.getCode().setText(text);
        }
        section.setEntry(entries);
        composition.addSection(section);
    }
}