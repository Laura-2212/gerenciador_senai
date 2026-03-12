package com.example.gerenciador_senai.config;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.gerenciador_senai.model.AtivoPatrimonial;
import com.example.gerenciador_senai.model.Categoria;
import com.example.gerenciador_senai.model.Funcionario;
import com.example.gerenciador_senai.model.FuncionarioAutenticado;
import com.example.gerenciador_senai.model.Material;
import com.example.gerenciador_senai.model.StatusAtivo;
import com.example.gerenciador_senai.model.TipoMovimentacao;
import com.example.gerenciador_senai.repository.CategoriaRepository;
import com.example.gerenciador_senai.repository.FuncionarioAutenticadoRepository;
import com.example.gerenciador_senai.repository.FuncionarioRepository;
import com.example.gerenciador_senai.repository.MaterialRepository;
import com.example.gerenciador_senai.repository.MovimentacaoEstoqueRepository;
import com.example.gerenciador_senai.service.AtivoPatrimonialService;
import com.example.gerenciador_senai.service.MovimentacaoEstoqueService;

@Configuration
public class DataInitializer {

    // cria dados iniciais para facilitar os testes
    @Bean
    CommandLineRunner initData(FuncionarioAutenticadoRepository funcionarioAutenticadoRepository,
            FuncionarioRepository funcionarioRepository,
            CategoriaRepository categoriaRepository,
            MaterialRepository materialRepository,
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            AtivoPatrimonialService ativoPatrimonialService) {
        return args -> {
            // cria os nifs liberados para cadastro
            // o nome e o nif daqui precisam ser usados iguais na tela de cadastro
            salvarFuncionarioAutorizado(funcionarioAutenticadoRepository, "Administrador Senai", "1001");
            salvarFuncionarioAutorizado(funcionarioAutenticadoRepository, "Tecnico Laboratorio", "1002");
            // para liberar outro nif, adicione outra chamada igual aqui
            // exemplo: salvarFuncionarioAutorizado(funcionarioAutenticadoRepository, "Novo Nome", "1003");

            // cria o usuario inicial para o primeiro login
            if (funcionarioRepository.count() == 0) {
                Funcionario funcionario = new Funcionario();
                funcionario.setNome("Administrador Senai");
                funcionario.setNif("1001");
                funcionario.setSenha("1234");
                funcionario.setAtivo(true);
                funcionarioRepository.save(funcionario);
            }

            // cria categorias iniciais
            if (categoriaRepository.count() == 0) {
                Categoria informatica = new Categoria();
                informatica.setNome("Informatica");
                informatica.setDescricao("equipamentos e perifericos");
                categoriaRepository.save(informatica);

                Categoria laboratorio = new Categoria();
                laboratorio.setNome("Laboratorio");
                laboratorio.setDescricao("materiais de uso tecnico");
                categoriaRepository.save(laboratorio);

                Categoria limpeza = new Categoria();
                limpeza.setNome("Limpeza");
                limpeza.setDescricao("itens de apoio e higienizacao");
                categoriaRepository.save(limpeza);
            }

            // cria materiais iniciais
            if (materialRepository.count() == 0) {
                Categoria informatica = categoriaRepository.findAllByOrderByNomeAsc().stream()
                        .filter(categoria -> categoria.getNome().equalsIgnoreCase("Informatica"))
                        .findFirst()
                        .orElseThrow();
                Categoria laboratorio = categoriaRepository.findAllByOrderByNomeAsc().stream()
                        .filter(categoria -> categoria.getNome().equalsIgnoreCase("Laboratorio"))
                        .findFirst()
                        .orElseThrow();

                Material pc = new Material();
                pc.setNome("pc");
                pc.setDescricao("pc para uso em sala");
                pc.setUnidadeMedida("unidade");
                pc.setQuantidadeMinima(6);
                pc.setQuantidadeEmEstoque(0);
                pc.setCategoria(informatica);
                materialRepository.save(pc);

                Material multimetro = new Material();
                multimetro.setNome("Multimetro");
                multimetro.setDescricao("equipamento de medicao");
                multimetro.setUnidadeMedida("unidade");
                multimetro.setQuantidadeMinima(3);
                multimetro.setQuantidadeEmEstoque(0);
                multimetro.setCategoria(laboratorio);
                materialRepository.save(multimetro);
            }

            // cria entradas iniciais no estoque (CORRIGIDO)
            if (movimentacaoEstoqueRepository.count() == 0) {
                Material multimetro = materialRepository.findAllByOrderByNomeAsc().stream()
                        .filter(material -> material.getNome().equalsIgnoreCase("Multimetro"))
                        .findFirst()
                        .orElseThrow();

                Material pc = materialRepository.findAllByOrderByNomeAsc().stream()
                        .filter(material -> material.getNome().equalsIgnoreCase("pc"))
                        .findFirst()
                        .orElseThrow();

                movimentacaoEstoqueService.salvar(null, pc.getId(), TipoMovimentacao.ENTRADA, 10,
                        "entrada inicial do sistema", "seed inicial");
                
                movimentacaoEstoqueService.salvar(null, multimetro.getId(), TipoMovimentacao.ENTRADA, 5,
                        "entrada inicial do sistema", "seed inicial");
            }

            // cria ativos patrimoniais iniciais
            if (ativoPatrimonialService.contarAtivos() == 0) {
                AtivoPatrimonial teclado = new AtivoPatrimonial();
                teclado.setCodigoPatrimonio("PAT-001");
                teclado.setNome("teclado");
                teclado.setDescricao("teclado para uso em sala");
                teclado.setLocalizacao("laboratorio 1");
                teclado.setResponsavel("Coordenacao");
                teclado.setStatusAtivo(StatusAtivo.EM_USO);
                teclado.setDataAquisicao(LocalDate.of(2025, 2, 10));
                teclado.setValorAquisicao(new BigDecimal("4200.00"));
                ativoPatrimonialService.salvar(teclado);

                AtivoPatrimonial computador = new AtivoPatrimonial();
                computador.setCodigoPatrimonio("PAT-002");
                computador.setNome("computador");
                computador.setDescricao("computador para uso em sala");
                computador.setLocalizacao("Laboratorio 2");
                computador.setResponsavel("Tecnico Laboratorio");
                computador.setStatusAtivo(StatusAtivo.DISPONIVEL);
                computador.setDataAquisicao(LocalDate.of(2022, 5, 15));
                computador.setValorAquisicao(new BigDecimal("3500.00"));
                ativoPatrimonialService.salvar(computador);
            }
        };
    }

    private void salvarFuncionarioAutorizado(FuncionarioAutenticadoRepository funcionarioAutenticadoRepository,
            String nome, String nif) {
        if (funcionarioAutenticadoRepository.findByNifAndAtivoTrue(nif).isPresent()) {
            return;
        }

        FuncionarioAutenticado funcionarioAutenticado = new FuncionarioAutenticado();
        funcionarioAutenticado.setNome(nome);
        funcionarioAutenticado.setNif(nif);
        funcionarioAutenticado.setAtivo(true);
        funcionarioAutenticadoRepository.save(funcionarioAutenticado);
    }
}