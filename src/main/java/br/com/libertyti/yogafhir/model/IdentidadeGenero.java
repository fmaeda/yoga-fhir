package br.com.libertyti.yogafhir.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentidadeGenero {

    DESCONHECIDO("UNK", "unknown"),
    NAO_BINARIO("33791000087105", "Identifies as nonbinary gender (finding)"),
    MASCULINO("446151000124109", "Identifies as male gender (finding)"),
    FEMININO("446141000124107", "Identifies as female gender (finding)"),
    TRANSEXUAL_FEMININO_PARA_MASCULINO("407377005", "Female-to-male transsexual (finding)"),
    TRANSEXUAL_MASCULINO_PARA_FEMININO("407376001", "Male-to-female transsexual (finding)"),
    NAO_RESPONDEU("asked-declined", "Asked But Declined");

    private final String loinc;
    private final String description;

}
