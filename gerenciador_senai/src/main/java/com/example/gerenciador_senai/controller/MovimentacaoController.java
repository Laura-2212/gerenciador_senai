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

import com.example.gerenciador_senai.model.MovimentacaoEstoque;
import com.example.gerenciador_senai.model.TipoMovimentacao;
import com.example.gerenciador_senai.service.MaterialService;
import com.example.gerenciador_senai.service.MovimentacaoEstoqueService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/movimentacoes")
public class MovimentacaoController {

	@Autowired
	private MovimentacaoEstoqueService movimentacaoEstoqueService;

	@Autowired
	private MaterialService materialService;

	@GetMapping
	public ModelAndView listar(HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "acesso nao permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/movimentacoes");
		mv.addObject("movimentacao", new MovimentacaoEstoque());
		mv.addObject("movimentacoes", movimentacaoEstoqueService.listarTodas());
		mv.addObject("materiais", materialService.listarTodos());
		mv.addObject("tiposMovimentacao", TipoMovimentacao.values());
		return mv;
	}

	@GetMapping("/editar/{id}")
	public ModelAndView editar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso não permitido 🙅🏼‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/movimentacoes");
		mv.addObject("movimentacao", movimentacaoEstoqueService.buscarPorId(id));
		mv.addObject("movimentacoes", movimentacaoEstoqueService.listarTodas());
		mv.addObject("materiais", materialService.listarTodos());
		mv.addObject("tiposMovimentacao", TipoMovimentacao.values());
		return mv;
	}

	@PostMapping("/salvar")
	public String salvar(@RequestParam(required = false) Long id, @RequestParam(required = false) Long materialId,
			@RequestParam(required = false) TipoMovimentacao tipoMovimentacao,
			@RequestParam(required = false) Integer quantidade,
			@RequestParam(required = false) String observacao, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso nao permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			String responsavel = String.valueOf(session.getAttribute("nomeUsuario"));
			movimentacaoEstoqueService.salvar(id, materialId, tipoMovimentacao, quantidade, observacao, responsavel);
			redirectAttributes.addFlashAttribute("msg", "Movimentacao Salva com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/movimentacoes";
	}

	@PostMapping("/{id}/excluir")
	public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
		if (!usuarioLogado(session)) {
			redirectAttributes.addFlashAttribute("msg", "Acesso não Permitido 🙅🏼‍♀️");
			redirectAttributes.addFlashAttribute("classe", "vermelho");
			return "redirect:/login";
		}

		try {
			movimentacaoEstoqueService.excluir(id);
			redirectAttributes.addFlashAttribute("msg", "Movimentacao Excluída com Sucesso!!");
			redirectAttributes.addFlashAttribute("classe", "verde");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("msg", ex.getMessage());
			redirectAttributes.addFlashAttribute("classe", "vermelho");
		}

		return "redirect:/app/movimentacoes";
	}

	private boolean usuarioLogado(HttpSession session) {
		return session.getAttribute("usuarioLogado") != null
				&& Boolean.TRUE.equals(session.getAttribute("usuarioLogado"));
	}
}
