package com.dvfs.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dvfs.pontointeligente.api.entities.Empresa;

public class EmpresaRepositoryTest extends TestHelper{
	
	@Before
	public void setUp() throws Exception{
		gerarEmpresa();
	}
	
	@Test
	public void testBuscarPorCnpjValido() {
		Empresa empresa = this.empresaRepository.findByCnpj(CNPJ);
		assertEquals(CNPJ, empresa.getCnpj());
	}
	
	@Test
	public void testBuscarPorCnpjInvalido() {
		Empresa empresa = this.empresaRepository.findByCnpj(CNPJ.concat("1"));
		assertNull(empresa);
	}
	
	@After
	public final void tearDown() {
		this.empresaRepository.deleteAll();
	}

}
