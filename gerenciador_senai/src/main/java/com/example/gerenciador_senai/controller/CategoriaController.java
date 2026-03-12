package com.example.gerenciador_senai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gerenciador_senai.model.Categoria;
import com.example.gerenciador_senai.service.CategoriaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping
	public ModelAndView listar(HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/categorias");
		mv.addObject("categoria", new Categoria());
		mv.addObject("categorias", categoriaService.listarTodas());
		return mv;
	}

	@GetMapping("/editar/{id}")
	public ModelAndView editar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/categorias");
		mv.addObject("categoria", categoriaService.buscarPorId(id));
		mv.addObject("categorias", categoriaService.listarTodas());
		return mv;
	}

	@PostMapping("/salvar")
	public String salvar(Categoria categoria, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			categoriaService.salvar(categoria);
			redirectAttributes.addFlashAttribute("msg", "Categoria Salva com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/categorias";
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não Permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			categoriaService.excluir(id);
			redirectAttributes.addFlashAttribute("msg", "Categoria Excluida com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/categorias";
	}

	private boolean usuarioLogado(HttpSession session) {
		return session.getAttribute("usuarioLogado") != null
				&& Boolean.TRUE.equals(session.getAttribute("usuarioLogado"));
	}
}
