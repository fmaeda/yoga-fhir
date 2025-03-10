package ca.hedman.springboothapifhirsimpleserver.domain;

import org.hl7.fhir.r4.model.codesystems.CompositionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record RAC(
        String id,
        CompositionStatus status,
        String title,
        LocalDateTime dateTime,
        CompositionType type,
        List<Category> category,
        Subject subject,
        Encounter encounter,
        List<Author> author,
        List<Section> section
) {
    public record CompositionType(List<Coding> coding, String text) {
    }

    public record Category(List<Coding> coding, String text) {
    }

    public record Coding(String code, String display) {
    }

    public record Subject(String reference) {
    }

    public record Encounter(String reference) {
    }

    public record Author(String reference) {
    }

    public record Section(String title, SectionCode code, List<Entry> entry) {
        public record SectionCode(List<Coding> coding, String text) {
        }

        public record Entry(String reference) {
        }
    }
}

