package com.example.gerenciador_senai.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.Funcionario;

public interface FuncionarioRepository extends CrudRepository<Funcionario, Long> {

	Optional<Funcionario> findByNifAndAtivoTrue(String nif);

	boolean existsByNif(String nif);

	long countByAtivoTrue();
}
