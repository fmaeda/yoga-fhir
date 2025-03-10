package br.com.libertyti.yogafhir.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RacaCor {

    SEM_INFORMACAO("99", "Sem Informação"),
    AMARELA("04", "Amarela"),
    PARDA("03", "Parda"),
    PRETA("02", "Preta"),
    BRANCA("01", "Branca"),
    INDIGENA("05", "Indígena");

    private final String code;
    private final String description;

}
