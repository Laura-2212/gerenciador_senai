package com.example.gerenciador_senai.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gerenciador_senai.model.Material;
import com.example.gerenciador_senai.model.MovimentacaoEstoque;
import com.example.gerenciador_senai.model.TipoMovimentacao;
import com.example.gerenciador_senai.repository.MaterialRepository;
import com.example.gerenciador_senai.repository.MovimentacaoEstoqueRepository;

@Service
public class MovimentacaoEstoqueService {

	private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
	private final MaterialRepository materialRepository;

	public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
			MaterialRepository materialRepository) {
		this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
		this.materialRepository = materialRepository;
	}

	public List<MovimentacaoEstoque> listarTodas() {
		return movimentacaoEstoqueRepository.findAllByOrderByDataMovimentacaoDescIdDesc();
	}

	public List<MovimentacaoEstoque> listarRecentes() {
		return movimentacaoEstoqueRepository.findTop5ByOrderByDataMovimentacaoDescIdDesc();
	}

	public MovimentacaoEstoque buscarPorId(Long id) {
		return movimentacaoEstoqueRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("movimentacao não encontrada!!"));
	}

	@Transactional
	public MovimentacaoEstoque salvar(Long id, Long materialId, TipoMovimentacao tipoMovimentacao, Integer quantidade,
			String observacao, String responsavel) {
		if (materialId == null || tipoMovimentacao == null || quantidade == null || quantidade <= 0) {
			throw new IllegalArgumentException("preencha material, tipo e quantidade");
		}

		Material materialNovo = materialRepository.findById(materialId)
				.orElseThrow(() -> new IllegalArgumentException("material não encontrado!!"));

		MovimentacaoEstoque movimentacao;
		int efeitoAnterior = 0;

		if (id == null) {
			movimentacao = new MovimentacaoEstoque();
			movimentacao.setDataMovimentacao(LocalDateTime.now());
		} else {
			movimentacao = buscarPorId(id);
			efeitoAnterior = calcularEfeito(movimentacao.getTipoMovimentacao(), movimentacao.getQuantidade());

			if (!movimentacao.getMaterial().getId().equals(materialNovo.getId())) {
				Material materialAnterior = movimentacao.getMaterial();
				int estoqueMaterialAnterior = materialAnterior.getQuantidadeEmEstoque() - efeitoAnterior;

				if (estoqueMaterialAnterior < 0) {
					throw new IllegalArgumentException("A alteracao deixaria o estoque do material anterior negativo!!");
				}

				materialAnterior.setQuantidadeEmEstoque(estoqueMaterialAnterior);
				materialRepository.save(materialAnterior);
				efeitoAnterior = 0;
			}
		}

		int estoqueAtual = materialNovo.getQuantidadeEmEstoque();
		int efeitoNovo = calcularEfeito(tipoMovimentacao, quantidade);
		int estoqueFinal = estoqueAtual - efeitoAnterior + efeitoNovo;

		if (estoqueFinal < 0) {
			throw new IllegalArgumentException("A saida informada deixa o estoque negativo!!");
		}

		materialNovo.setQuantidadeEmEstoque(estoqueFinal);
		materialRepository.save(materialNovo);

		movimentacao.setMaterial(materialNovo);
		movimentacao.setTipoMovimentacao(tipoMovimentacao);
		movimentacao.setQuantidade(quantidade);
		movimentacao.setObservacao(limparOpcional(observacao));
		movimentacao.setResponsavel(limparResponsavel(responsavel, movimentacao.getResponsavel()));
		return movimentacaoEstoqueRepository.save(movimentacao);
	}

	@Transactional
	public void excluir(Long id) {
		MovimentacaoEstoque movimentacao = buscarPorId(id);
		Material material = movimentacao.getMaterial();
		int efeito = calcularEfeito(movimentacao.getTipoMovimentacao(), movimentacao.getQuantidade());
		int estoqueFinal = material.getQuantidadeEmEstoque() - efeito;

		if (estoqueFinal < 0) {
			throw new IllegalArgumentException("não e possivel excluir a movimentacao porque o estoque ficaria negativo!!");
		}

		material.setQuantidadeEmEstoque(estoqueFinal);
		materialRepository.save(material);
		movimentacaoEstoqueRepository.delete(movimentacao);
	}

	public long contarMovimentacoes() {
		return movimentacaoEstoqueRepository.count();
	}

	private int calcularEfeito(TipoMovimentacao tipoMovimentacao, Integer quantidade) {
		return tipoMovimentacao == TipoMovimentacao.ENTRADA ? quantidade : quantidade * -1;
	}

	private String limparOpcional(String valor) {
		if (valor == null) {
			return null;
		}

		String texto = valor.trim();
		return texto.isBlank() ? null : texto;
	}

	private String limparResponsavel(String valor, String valorAtual) {
		String texto = limparOpcional(valor);

		if (texto != null) {
			return texto;
		}

		return valorAtual == null || valorAtual.isBlank() ? "usuario do sistema" : valorAtual;
	}
}
