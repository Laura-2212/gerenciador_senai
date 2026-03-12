package com.example.gerenciador_senai.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.gerenciador_senai.model.AtivoPatrimonial;

public interface AtivoPatrimonialRepository extends CrudRepository<AtivoPatrimonial, Long> {

	List<AtivoPatrimonial> findAllByOrderByNomeAsc();

	boolean existsByCodigoPatrimonioIgnoreCase(String codigoPatrimonio);

	boolean existsByCodigoPatrimonioIgnoreCaseAndIdNot(String codigoPatrimonio, Long id);
}
