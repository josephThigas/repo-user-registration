package com.example.userregistrationservice.dto;

public record EnderecoResponse(
        String cep,
        String logradouro,
        String localidade,
        String uf,
        String estado
) {
}
