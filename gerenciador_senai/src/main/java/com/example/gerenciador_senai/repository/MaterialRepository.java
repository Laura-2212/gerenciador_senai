package com.example.gerenciador_senai.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.Material;

public interface MaterialRepository extends CrudRepository<Material, Long> {

	List<Material> findAllByOrderByNomeAsc();

	boolean existsByCategoriaId(Long categoriaId);
}
