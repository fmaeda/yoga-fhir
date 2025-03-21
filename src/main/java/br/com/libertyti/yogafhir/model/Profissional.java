package br.com.libertyti.yogafhir.model;

public record Profissional(String id, String name,
                           String cns,
                           ValueObject cbo,
                           Conselho conselho) {
    public record Conselho(String numero, String acronimo, String uf) {
    }
}
