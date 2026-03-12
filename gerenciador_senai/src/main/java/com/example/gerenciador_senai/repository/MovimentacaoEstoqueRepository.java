package com.example.gerenciador_senai.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.MovimentacaoEstoque;

public interface MovimentacaoEstoqueRepository extends CrudRepository<MovimentacaoEstoque, Long> {

	List<MovimentacaoEstoque> findAllByOrderByDataMovimentacaoDescIdDesc();

	List<MovimentacaoEstoque> findTop5ByOrderByDataMovimentacaoDescIdDesc();

	boolean existsByMaterialId(Long materialId);
}
