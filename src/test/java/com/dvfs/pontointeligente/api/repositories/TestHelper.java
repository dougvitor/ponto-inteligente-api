package com.dvfs.pontointeligente.api.repositories;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dvfs.pontointeligente.api.entities.Empresa;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.entities.Lancamento;
import com.dvfs.pontointeligente.api.enums.PerfilEnum;
import com.dvfs.pontointeligente.api.enums.TipoEnum;
import com.dvfs.pontointeligente.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore
public class TestHelper {
	
	@Autowired
	EmpresaRepository empresaRepository;
	
	@Autowired
	FuncionarioRepository funcionarioRepository;
	
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	static final String CNPJ = "51463645000010";
	
	static final String CPF = "35485161815";
	
	static final String EMAIL = "fulano@email.com";
	
	void gerarEmpresa()throws Exception{
		Empresa empresa = newEmpresa();
		this.empresaRepository.save(empresa);
	}

	void gerarFuncionario()throws Exception{
		Funcionario funcionario = newFuncionario();
		this.funcionarioRepository.save(funcionario);
	}
	
	void gerarLancamento()throws Exception{
		Lancamento lancamento = newLancamento();
		this.lancamentoRepository.save(lancamento);
	}
	
	private Empresa newEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("Tester Corporation");
		empresa.setCnpj(CNPJ);
		return empresa;
	}
	
	private Funcionario newFuncionario() {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Fulano");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123"));
		funcionario.setCpf(CPF);
		funcionario.setEmail(EMAIL);
		funcionario.setQtdHorasAlmoco(Float.valueOf(100));
		funcionario.setQtdHorasTrabalhoDia(Float.valueOf(200));
		funcionario.setValorHora(BigDecimal.TEN);
		funcionario.setEmpresa(this.empresaRepository.findByCnpj(CNPJ));
		return funcionario;
	}
	
	private Lancamento newLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(new Date());
		lancamento.setDescricao("Iniciando o almoço");
		lancamento.setLocalizacao("Refeitório");
		lancamento.setTipo(TipoEnum.INICIO_ALMOCO);
		lancamento.setFuncionario(this.funcionarioRepository.findByCpf(CPF));
		return lancamento;
	}

}
