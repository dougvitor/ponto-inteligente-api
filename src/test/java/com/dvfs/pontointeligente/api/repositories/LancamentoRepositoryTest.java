package com.dvfs.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.dvfs.pontointeligente.api.entities.Lancamento;

public class LancamentoRepositoryTest extends TestHelper{
	
	private Long funcionarioId;
	
	@Before
	public void setUp() throws Exception{
		gerarEmpresa();
		gerarFuncionario();
		
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		gerarLancamento();
		
		funcionarioId = this.funcionarioRepository.findByCpf(CPF).getId();
	}
	
	@Test
	public void testBuscarLancamentosPorFuncionarioId() {
		List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);
		assertEquals(11, lancamentos.size());
	}
	
	@Test
	public void testBuscarLancamentosPorFuncionarioIdPaginado() {
		PageRequest page = PageRequest.of(0, 5);
		Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);
		assertEquals(6, lancamentos.getNumberOfElements());
	}
	
	@After
	public void tearDown() throws Exception{
		this.lancamentoRepository.deleteAll();
		this.funcionarioRepository.deleteAll();
		this.empresaRepository.deleteAll();
	}

}
