package com.dvfs.pontointeligente.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dvfs.pontointeligente.api.dtos.CadastroPJDto;
import com.dvfs.pontointeligente.api.entities.Empresa;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.enums.PerfilEnum;
import com.dvfs.pontointeligente.api.response.Response;
import com.dvfs.pontointeligente.api.services.EmpresaService;
import com.dvfs.pontointeligente.api.services.FuncionarioService;
import com.dvfs.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CadastroPJController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPJController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPJDto>> cadastrar(@Valid @RequestBody CadastroPJDto cadastroPJDto, BindingResult result) throws NoSuchAlgorithmException{
		LOG.info("Cadastrando PJ: {}", cadastroPJDto.toString());
		
		Response<CadastroPJDto> response = new Response<>();
		
		validarDadosExistentes(cadastroPJDto, result);
		
		if(result.hasErrors()) {
			LOG.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Empresa empresa = this.parseDtoToEmpresa(cadastroPJDto);
		
		Funcionario funcionario = this.parseDtoToFuncionario(cadastroPJDto, result);
		
		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.parseCadastroPJDto(funcionario));
		
		return ResponseEntity.ok(response);
	}

	private Empresa parseDtoToEmpresa(@Valid CadastroPJDto cadastroPJDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDto.getCnpj());
		empresa.setRazaoSocial(cadastroPJDto.getRazaoSocial());
		return empresa;
	}
	
	private Funcionario parseDtoToFuncionario(@Valid CadastroPJDto cadastroPJDto, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDto.getNome());
		funcionario.setEmail(cadastroPJDto.getEmail());
		funcionario.setCpf(cadastroPJDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDto.getSenha()));
		return funcionario;
	}
	
	private CadastroPJDto parseCadastroPJDto(Funcionario funcionario) {
		CadastroPJDto dto = new CadastroPJDto();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setSenha(funcionario.getSenha());
		dto.setCpf(funcionario.getCpf());
		dto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		
		return dto;
	}

	private void validarDadosExistentes(CadastroPJDto cadastroPJDto, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJDto.getCnpj())
			.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));
		
		this.funcionarioService.buscarPorCpf(cadastroPJDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
		
		this.funcionarioService.buscarPorEmail(cadastroPJDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
		
	}
}
