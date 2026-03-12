package com.example.gerenciador_senai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gerenciador_senai.model.Material;
import com.example.gerenciador_senai.service.CategoriaService;
import com.example.gerenciador_senai.service.MaterialService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/materiais")
public class MaterialController {

	@Autowired
	private MaterialService materialService;

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping
	public ModelAndView listar(HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/materiais");
		mv.addObject("material", new Material());
		mv.addObject("materiais", materialService.listarTodos());
		mv.addObject("categorias", categoriaService.listarTodas());
		return mv;
	}

	@GetMapping("/editar/{id}")
	public ModelAndView editar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/materiais");
		mv.addObject("material", materialService.buscarPorId(id));
		mv.addObject("materiais", materialService.listarTodos());
		mv.addObject("categorias", categoriaService.listarTodas());
		return mv;
	}

	@PostMapping("/salvar")
	public String salvar(Material material, @RequestParam(required = false) Long categoriaId, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			materialService.salvar(material, categoriaId);
			redirectAttributes.addFlashAttribute("msg", "material salvo com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/materiais";
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso Não permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			materialService.excluir(id);
			redirectAttributes.addFlashAttribute("msg", "material Excluido com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/materiais";
	}

	private boolean usuarioLogado(HttpSession session) {
		return session.getAttribute("usuarioLogado") != null
				&& Boolean.TRUE.equals(session.getAttribute("usuarioLogado"));
	}
}
