package com.example.gerenciador_senai.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.Categoria;

public interface CategoriaRepository extends CrudRepository<Categoria, Long> {

	List<Categoria> findAllByOrderByNomeAsc();

	boolean existsByNomeIgnoreCase(String nome);

	boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
