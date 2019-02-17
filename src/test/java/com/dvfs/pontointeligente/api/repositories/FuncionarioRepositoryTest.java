package com.dvfs.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.dvfs.pontointeligente.api.entities.Funcionario;

public class FuncionarioRepositoryTest extends TestHelper{
	
	@Before
	public void setUp() throws Exception{
		gerarEmpresa();
		gerarFuncionario();
	}
	
	@Test
	public void testBuscarFuncionarioPorEmail() {
		Funcionario funcionario = this.funcionarioRepository.findByEmail(EMAIL);
		assertEquals(EMAIL, funcionario.getEmail());
	}
	
	@Test
	public void testBuscarFuncionarioPorCPF() {
		Funcionario funcionario = this.funcionarioRepository.findByCpf(CPF);
		assertEquals(CPF, funcionario.getCpf());
	}
	
	@Test
	public void testBuscarFuncionarioPorCPFOuEmailInvalido() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, "email@invalido.com");
		assertNotNull(funcionario);
	}
	
	@Test
	public void testBuscarFuncionarioPorCPFEEmailInvalidos() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF.concat("1"), "email@invalido.com");
		assertNull(funcionario);
	}
	
	@After
	public void tearDown() {
		this.funcionarioRepository.deleteAll();
		this.empresaRepository.deleteAll();
	}

}
