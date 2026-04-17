package com.example.userregistrationservice.dto;

import java.util.List;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        List<EnderecoResponse> enderecos
) {
}
