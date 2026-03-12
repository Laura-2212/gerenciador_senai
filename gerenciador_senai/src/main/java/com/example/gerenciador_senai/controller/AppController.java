package com.example.gerenciador_senai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.gerenciador_senai.service.AtivoPatrimonialService;
import com.example.gerenciador_senai.service.CategoriaService;
import com.example.gerenciador_senai.service.FuncionarioService;
import com.example.gerenciador_senai.service.MaterialService;
import com.example.gerenciador_senai.service.MovimentacaoEstoqueService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AppController {

	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private MaterialService materialService;

	@Autowired
	private MovimentacaoEstoqueService movimentacaoEstoqueService;

	@Autowired
	private AtivoPatrimonialService ativoPatrimonialService;

	@GetMapping("/app")
	public ModelAndView dashboard(HttpSession session, RedirectAttributes attributes) {
		if (!usuarioLogado(session)) {
			attributes.addFlashAttribute("msg", "Acesso Não Permitido🙅🏻‍♀️");
			attributes.addFlashAttribute("classe", "vermelho");
			return new ModelAndView("redirect:/login");
		}

		ModelAndView mv = new ModelAndView("app/index");
		mv.addObject("nomeUsuario", session.getAttribute("nomeUsuario"));
		mv.addObject("nifUsuario", session.getAttribute("nifUsuario"));
		mv.addObject("totalFuncionarios", funcionarioService.contarFuncionariosAtivos());
		mv.addObject("totalCategorias", categoriaService.contarCategorias());
		mv.addObject("totalMateriais", materialService.contarMateriais());
		mv.addObject("totalMovimentacoes", movimentacaoEstoqueService.contarMovimentacoes());
		mv.addObject("totalAtivos", ativoPatrimonialService.contarAtivos());
		mv.addObject("movimentacoesRecentes", movimentacaoEstoqueService.listarRecentes());
		mv.addObject("materiaisComEstoqueBaixo", materialService.listarEstoqueBaixo());
		return mv;
	}

	private boolean usuarioLogado(HttpSession session) {
		return session.getAttribute("usuarioLogado") != null
				&& Boolean.TRUE.equals(session.getAttribute("usuarioLogado"));
	}
}
