package com.dvfs.pontointeligente.api.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.repositories.FuncionarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {
	
	@MockBean
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Before
	public void setUp() {
		BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong())).willReturn(Optional.ofNullable(new Funcionario()));
		BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString())).willReturn(new Funcionario());
	}
	
	@Test
	public void testPersistirFuncionario() {
		Funcionario funcionario = this.funcionarioService.persistir(new Funcionario());
		assertNotNull(funcionario);
	}
	
	@Test
	public void testBuscarFuncionarioById() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(1L);
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void testBuscarFuncionarioByEmail() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorEmail("email");
		assertTrue(funcionario.isPresent());
	}
	
	@Test
	public void testBuscarFuncionarioByCPF() {
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorCpf("54327235489");
		assertTrue(funcionario.isPresent());
	}
	

}
