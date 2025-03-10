package ca.hedman.springboothapifhirsimpleserver.domain;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;

public class RACFactory {

    public RACFactory() {
    }

    public static RAC create(Bundle bundle) {
        // procurar por Composition (RAC) para começar o parse
        return null;
    }

}
