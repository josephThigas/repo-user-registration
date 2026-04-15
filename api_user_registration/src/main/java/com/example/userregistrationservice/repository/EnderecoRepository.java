package com.example.userregistrationservice.repository;

import com.example.userregistrationservice.model.Endereco;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends CrudRepository<Endereco, Long> {
    List<Endereco> findByUsuarioId(Long usuarioId);
}
