package br.com.libertyti.yogafhir.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

@JsonSerialize
public record RegistroAtendimentoClinico(
        String id,
        String status,
        String titulo,
        LocalDateTime data,
        ValueObject tipo,
        List<ValueObject> categorias,
        Paciente paciente,
        Atendimento atendimento,
        Profissional autor,
        List<Secoes> secoes
) {
    public record Atendimento(
            String id,
            String status,
            String dataInicio,
            String dataFim,
            ValueObject ato,
            ValueObject prioridadeDoAto,
            List<ValueObject> tipos,
            ValueObject tipoServico,
            String pacienteId,
            List<Participante> participantes,
            Periodo periodo,
            List<CID> cids,
            List<Diagnostico> diagnosticos,
            Local local,
            Organizacao organizacao,
            List<ValueObject> procedimentos,
            List<Alergia> alergias
    ) {
        public record Participante(List<ValueObject> tipos, String profissionalId) {
        }

        public record Diagnostico(Condicao condicao, ValueObject tipo, Integer classificacao) {
        }

        public record Alergia(String categoria, ValueObject code, String manifestacao, String grauCerteza, String criticidade, LocalDateTime registradoEm, ValueObject statusClinico, ValueObject statusDeVerificacao){}
    }

    public record Secoes(String titulo, ValueObject tipo, List<String> referencias) {
    }

    public record Condicao(String id, CID cid, ValueObject statusClinico, ValueObject statusDeVerificacao,
                           List<ValueObject> categorias, ValueObject severidade, List<ValueObject> localAnatomico,
                           String inicioEstimado, LocalDateTime dataRegistro, String recorderId, String asserterId,
                           List<Evidencia> evidencias, InformacoesAdicionais informacoesAdicionais) {
        public record Evidencia(List<ValueObject> cids, String referenciaId) {
        }

        public record InformacoesAdicionais(String autorId, String texto, LocalDateTime data) {
        }
    }

    public record Local(String id, String descricao) {
    }

    public record Organizacao(String id, String descricao, String cnes) {
    }

    public record Periodo(LocalDateTime inicio, LocalDateTime fim) {
    }

    public record CID(String codigo, String descricao, String nota) {
    }
}
