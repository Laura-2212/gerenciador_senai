package com.example.gerenciador_senai.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ativos_patrimoniais")
public class AtivoPatrimonial implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String codigoPatrimonio;

	private String nome;

	private String descricao;

	private String localizacao;

	private String responsavel;

	@Enumerated(EnumType.STRING)
	private StatusAtivo statusAtivo;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataAquisicao;

	private BigDecimal valorAquisicao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigoPatrimonio() {
		return codigoPatrimonio;
	}

	public void setCodigoPatrimonio(String codigoPatrimonio) {
		this.codigoPatrimonio = codigoPatrimonio;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public StatusAtivo getStatusAtivo() {
		return statusAtivo;
	}

	public void setStatusAtivo(StatusAtivo statusAtivo) {
		this.statusAtivo = statusAtivo;
	}

	public LocalDate getDataAquisicao() {
		return dataAquisicao;
	}

	public void setDataAquisicao(LocalDate dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	public BigDecimal getValorAquisicao() {
		return valorAquisicao;
	}

	public void setValorAquisicao(BigDecimal valorAquisicao) {
		this.valorAquisicao = valorAquisicao;
	}
}
