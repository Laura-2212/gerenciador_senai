package com.example.gerenciador_senai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.gerenciador_senai.model.AtivoPatrimonial;
import com.example.gerenciador_senai.repository.AtivoPatrimonialRepository;

@Service
public class AtivoPatrimonialService {

	private final AtivoPatrimonialRepository ativoPatrimonialRepository;

	public AtivoPatrimonialService(AtivoPatrimonialRepository ativoPatrimonialRepository) {
		this.ativoPatrimonialRepository = ativoPatrimonialRepository;
	}

	public List<AtivoPatrimonial> listarTodos() {
		return ativoPatrimonialRepository.findAllByOrderByNomeAsc();
	}

	public AtivoPatrimonial buscarPorId(Long id) {
		return ativoPatrimonialRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("ativo patrimonial não encontrado!"));
	}

	public AtivoPatrimonial salvar(AtivoPatrimonial form) {
		if (limpar(form.getCodigoPatrimonio()).isBlank() || limpar(form.getNome()).isBlank()) {
			throw new IllegalArgumentException("informe codigo e nome do ativo!");
		}

		AtivoPatrimonial ativo = form.getId() == null ? new AtivoPatrimonial() : buscarPorId(form.getId());
		ativo.setCodigoPatrimonio(limpar(form.getCodigoPatrimonio()));
		ativo.setNome(limpar(form.getNome()));
		ativo.setDescricao(limparOpcional(form.getDescricao()));
		ativo.setLocalizacao(limpar(form.getLocalizacao()));
		ativo.setResponsavel(limpar(form.getResponsavel()));
		ativo.setStatusAtivo(form.getStatusAtivo());
		ativo.setDataAquisicao(form.getDataAquisicao());
		ativo.setValorAquisicao(form.getValorAquisicao());
		return ativoPatrimonialRepository.save(ativo);
	}

	public void excluir(Long id) {
		ativoPatrimonialRepository.delete(buscarPorId(id));
	}

	public long contarAtivos() {
		return ativoPatrimonialRepository.count();
	}

	private String limpar(String valor) {
		return valor == null ? "" : valor.trim();
	}

	private String limparOpcional(String valor) {
		String texto = limpar(valor);
		return texto.isBlank() ? null : texto;
	}
}
