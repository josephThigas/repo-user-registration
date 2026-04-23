package com.example.userregistrationservice.dto;

public record EnderecoResponse(
        Long id,
        String cep,
        String logradouro,
        String localidade,
        String uf,
        String estado
) {
}
