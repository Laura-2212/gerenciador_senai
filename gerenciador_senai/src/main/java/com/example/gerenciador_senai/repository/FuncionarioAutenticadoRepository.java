package com.example.gerenciador_senai.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.FuncionarioAutenticado;

public interface FuncionarioAutenticadoRepository extends CrudRepository<FuncionarioAutenticado, Long> {

	boolean existsByNifAndNomeIgnoreCaseAndAtivoTrue(String nif, String nome);

	Optional<FuncionarioAutenticado> findByNifAndAtivoTrue(String nif);
}
