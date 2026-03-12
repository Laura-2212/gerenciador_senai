package com.example.gerenciador_senai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gerenciador_senai.model.Categoria;
import com.example.gerenciador_senai.repository.CategoriaRepository;
import com.example.gerenciador_senai.repository.MaterialRepository;

@Service
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final MaterialRepository materialRepository;

	public CategoriaService(CategoriaRepository categoriaRepository, MaterialRepository materialRepository) {
		this.categoriaRepository = categoriaRepository;
		this.materialRepository = materialRepository;
	}

	public List<Categoria> listarTodas() {
		return categoriaRepository.findAllByOrderByNomeAsc();
	}

	public Categoria buscarPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Não foi possivel localizar essa categoria"));
	}

	public Categoria salvar(Categoria form) {
		String nome = limpar(form.getNome());
		String descricao = limparOpcional(form.getDescricao());

		if (nome.isBlank()) {
			throw new IllegalArgumentException("informe o nome da categoria!!");
		}

		Categoria categoria = form.getId() == null ? new Categoria() : buscarPorId(form.getId());
		categoria.setNome(nome);
		categoria.setDescricao(descricao);
		return categoriaRepository.save(categoria);
	}

	public void excluir(Long id) {
		Categoria categoria = buscarPorId(id);

		if (materialRepository.existsByCategoriaId(id)) {
			throw new IllegalArgumentException("nao e possivel excluir a categoria porque existem materiais vinculados!!");
		}

		categoriaRepository.delete(categoria);
	}

	public long contarCategorias() {
		return categoriaRepository.count();
	}

	private String limpar(String valor) {
		return valor == null ? "" : valor.trim();
	}

	private String limparOpcional(String valor) {
		String texto = limpar(valor);
		return texto.isBlank() ? null : texto;
	}
}
