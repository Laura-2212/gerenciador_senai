package com.example.gerenciador_senai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gerenciador_senai.model.Categoria;
import com.example.gerenciador_senai.model.Material;
import com.example.gerenciador_senai.repository.CategoriaRepository;
import com.example.gerenciador_senai.repository.MaterialRepository;
import com.example.gerenciador_senai.repository.MovimentacaoEstoqueRepository;

@Service
public class MaterialService {

	private final MaterialRepository materialRepository;
	private final CategoriaRepository categoriaRepository;
	private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

	public MaterialService(MaterialRepository materialRepository, CategoriaRepository categoriaRepository,
			MovimentacaoEstoqueRepository movimentacaoEstoqueRepository) {
		this.materialRepository = materialRepository;
		this.categoriaRepository = categoriaRepository;
		this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
	}

	public List<Material> listarTodos() {
		return materialRepository.findAllByOrderByNomeAsc();
	}

	public Material buscarPorId(Long id) {
		return materialRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("material não encontrado"));
	}

	public Material salvar(Material form, Long categoriaId) {
		if (categoriaId == null) {
			throw new IllegalArgumentException("selecione uma categoria");
		}

		if (limpar(form.getNome()).isBlank()) {
			throw new IllegalArgumentException("informe o nome do material");
		}

		Categoria categoria = categoriaRepository.findById(categoriaId)
				.orElseThrow(() -> new IllegalArgumentException("categoria não encontrada"));

		Material material = form.getId() == null ? new Material() : buscarPorId(form.getId());
		int estoqueAtual = material.getQuantidadeEmEstoque() == null ? 0 : material.getQuantidadeEmEstoque();

		material.setNome(limpar(form.getNome()));
		material.setDescricao(limparOpcional(form.getDescricao()));
		material.setUnidadeMedida(limpar(form.getUnidadeMedida()));
		material.setQuantidadeMinima(form.getQuantidadeMinima() == null ? 0 : form.getQuantidadeMinima());
		material.setQuantidadeEmEstoque(estoqueAtual);
		material.setCategoria(categoria);
		return materialRepository.save(material);
	}

	public void excluir(Long id) {
		Material material = buscarPorId(id);

		if (movimentacaoEstoqueRepository.existsByMaterialId(id)) {
			throw new IllegalArgumentException("não e possivel excluir o material porque existem movimentacoes registradas");
		}

		materialRepository.delete(material);
	}

	public long contarMateriais() {
		return materialRepository.count();
	}

	public List<Material> listarEstoqueBaixo() {
		return materialRepository.findAllByOrderByNomeAsc().stream()
				.filter(material -> material.getQuantidadeMinima() != null && material.getQuantidadeMinima() > 0)
				.filter(material -> material.getQuantidadeEmEstoque() <= material.getQuantidadeMinima())
				.limit(5)
				.toList();
	}

	private String limpar(String valor) {
		return valor == null ? "" : valor.trim();
	}

	private String limparOpcional(String valor) {
		String texto = limpar(valor);
		return texto.isBlank() ? null : texto;
	}
}
