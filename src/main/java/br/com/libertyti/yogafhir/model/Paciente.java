package br.com.libertyti.yogafhir.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize
public record Paciente(
        String id,
        String cpf,
        String cns,
        String nome,
        Sexo sexo,
        RacaCor raca,
        IdentidadeGenero genero,
        Endereco enderecoNascimento,
        List<Endereco> enderecos,
        List<Contato> contatos
) {

    public record Endereco(
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cep,
            String uf,
            String cidade,
            String pais
    ) {}

    public record Contato(
            String nome,
            String telefone,
            String celular,
            String email,
            Endereco endereco
    ) {}
}
