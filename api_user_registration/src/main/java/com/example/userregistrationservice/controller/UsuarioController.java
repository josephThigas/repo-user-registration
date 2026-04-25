package com.example.userregistrationservice.controller;

import com.example.userregistrationservice.config.Logger;
import com.example.userregistrationservice.dto.UsuarioRequest;
import com.example.userregistrationservice.dto.UsuarioResponse;
import com.example.userregistrationservice.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "API de gerenciamento de usuários")
public class UsuarioController {

    Logger logger = Logger.getInstancia();
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário com dados pessoais e endereço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> criarUsuario(@RequestBody UsuarioRequest usuarioRequest){
        logger.info("Criando usuário: " + usuarioRequest.nome());
        usuarioService.salvarUsuario(usuarioRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados com seus endereços")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover usuário", description = "Remove um usuário e todos os seus endereços associados (com cascade)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        try {
            logger.info("Removendo usuário com ID: " + id);
            usuarioService.removerUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("Usuário não encontrado: " + id);
            return ResponseEntity.notFound().build();
        }
    }

        @PutMapping("/{id}")
        @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
                @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        public ResponseEntity<Void> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {
            try {
                logger.info("Atualizando usuário com ID: " + id);
                usuarioService.atualizarUsuario(id, usuarioRequest);
                return ResponseEntity.noContent().build();
            } catch (IllegalArgumentException e) {
                logger.error("Erro ao atualizar usuário: " + e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
}
