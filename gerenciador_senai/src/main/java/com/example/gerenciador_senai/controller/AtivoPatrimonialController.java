package com.example.gerenciador_senai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gerenciador_senai.model.AtivoPatrimonial;
import com.example.gerenciador_senai.model.StatusAtivo;
import com.example.gerenciador_senai.service.AtivoPatrimonialService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/ativos")
public class AtivoPatrimonialController {

	@Autowired
	private AtivoPatrimonialService ativoPatrimonialService;

	@GetMapping
	public ModelAndView listar(HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/ativos");
		mv.addObject("ativo", new AtivoPatrimonial());
		mv.addObject("ativos", ativoPatrimonialService.listarTodos());
		mv.addObject("statusAtivos", StatusAtivo.values());
		return mv;
	}

	@GetMapping("/editar/{id}")
	public ModelAndView editar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/ativos");
		mv.addObject("ativo", ativoPatrimonialService.buscarPorId(id));
		mv.addObject("ativos", ativoPatrimonialService.listarTodos());
		mv.addObject("statusAtivos", StatusAtivo.values());
		return mv;
	}

	@PostMapping("/salvar")
	public String salvar(AtivoPatrimonial ativo, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			ativoPatrimonialService.salvar(ativo);
			redirectAttributes.addFlashAttribute("msg", "Ativo Patrimonial Salvo com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/ativos";
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			ativoPatrimonialService.excluir(id);
			redirectAttributes.addFlashAttribute("msg", "Ativo Patrimonial Excluido com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/ativos";
	}

	private boolean usuarioLogado(HttpSession session) {
		return session.getAttribute("usuarioLogado") != null
				&& Boolean.TRUE.equals(session.getAttribute("usuarioLogado"));
	}
}
