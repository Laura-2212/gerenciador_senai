package com.example.gerenciador_senai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gerenciador_senai.model.Funcionario;
import com.example.gerenciador_senai.service.FuncionarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired
	private FuncionarioService funcionarioService;

	@GetMapping("/login")
	public String loginPage(HttpSession session) {
		if (Boolean.TRUE.equals(session.getAttribute("usuarioLogado"))) {
			return "redirect:/app";
		}

		return "auth/login";
	}

	@PostMapping("/auth/login")
	public String login(@RequestParam String nif, @RequestParam String senha, HttpSession session,
			RedirectAttributes attributes) {
		try {
			Funcionario funcionario = funcionarioService.autenticar(nif, senha);
			session.setAttribute("usuarioLogado", true);
			session.setAttribute("funcionarioId", funcionario.getId());
			session.setAttribute("nomeUsuario", funcionario.getNome());
			session.setAttribute("nifUsuario", funcionario.getNif());
			return "redirect:/app";
		} catch (IllegalArgumentException ex) {
			attributes.addFlashAttribute("msg", ex.getMessage());
			attributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}
	}

	@GetMapping("/cadastro")
	public String cadastroPage(HttpSession session) {
		if (Boolean.TRUE.equals(session.getAttribute("usuarioLogado"))) {
			return "redirect:/app";
		}

		return "auth/cadastro";
	}

	@PostMapping("/auth/cadastro")
	public String cadastro(@RequestParam String nome, @RequestParam String nif, @RequestParam String senha,
			RedirectAttributes redirectAttributes) {
		try {
			funcionarioService.cadastrar(nome, nif, senha);
			redirectAttributes.addFlashAttribute("msg", "Cadastro Realizado com Sucesso");
			redirectAttributes.addFlashAttribute("classe", "verde");
			return "redirect:/login";
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/cadastro";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
		session.invalidate();
		redirectAttributes.addFlashAttribute("msg", "Sessao Encerrada com Sucesso!!");
		redirectAttributes.addFlashAttribute("classe", "verde");
		return "redirect:/";
	}
}
