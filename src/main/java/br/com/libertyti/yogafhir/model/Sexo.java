package br.com.libertyti.yogafhir.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sexo {
    UNKNOWN("unknown", "Desconhecido"),
    MASCULINO("male", "Masculino"),
    FEMININO("female", "Feminino"),
    OUTROS("other", "Outros");

    final String code;
    final String description;

}
