package com.dvfs.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

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

import com.dvfs.pontointeligente.api.dtos.CadastroPFDto;
import com.dvfs.pontointeligente.api.entities.Empresa;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.enums.PerfilEnum;
import com.dvfs.pontointeligente.api.response.Response;
import com.dvfs.pontointeligente.api.services.EmpresaService;
import com.dvfs.pontointeligente.api.services.FuncionarioService;
import com.dvfs.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/cadastrar-pf")
@CrossOrigin(origins = "*")
public class CadastroPFController {
	
	private static final Logger LOG = LoggerFactory.getLogger(CadastroPFController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;

	@Autowired
	private EmpresaService empresaService;
	
	public CadastroPFController() {
		
	}
	
	@PostMapping
	public ResponseEntity<Response<CadastroPFDto>> cadastrar(@Valid @RequestBody CadastroPFDto cadastroPFDto, BindingResult result) throws NoSuchAlgorithmException{
		LOG.info("Cadastrando PF: {}", cadastroPFDto.toString());
		
		Response<CadastroPFDto> response = new Response<>();
		
		validarDadosExistentes(cadastroPFDto, result);
		
		if(result.hasErrors()) {
			LOG.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Funcionario funcionario = this.parseDtoToFuncionario(cadastroPFDto, result);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.parseCadastroPFDto(funcionario));
		
		return ResponseEntity.ok(response);
	}

	private Funcionario parseDtoToFuncionario(@Valid CadastroPFDto cadastroPFDto, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPFDto.getNome());
		funcionario.setEmail(cadastroPFDto.getEmail());
		funcionario.setCpf(cadastroPFDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPFDto.getSenha()));
		
		cadastroPFDto.getQtdHorasAlmoco().ifPresent(horasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(horasAlmoco)));
		cadastroPFDto.getQtdHorasTrabalhoDia().ifPresent(horasTrabalhadas -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(horasTrabalhadas)));
		cadastroPFDto.getValorHora().ifPresent(valorHora-> funcionario.setValorHora(new BigDecimal(valorHora)));
		
		empresaService.buscarPorCnpj(cadastroPFDto.getCnpj()).ifPresent(emp -> funcionario.setEmpresa(emp));
		
		return funcionario;
	}
	
	private CadastroPFDto parseCadastroPFDto(Funcionario funcionario) {
		CadastroPFDto dto = new CadastroPFDto();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setSenha(funcionario.getSenha());
		dto.setCpf(funcionario.getCpf());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		
		funcionario.getQtdHorasAlmocoOpt().ifPresent(horaAlmoco -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(horaAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(horasTrabalhadas -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(horasTrabalhadas))));
		funcionario.getValorHoraOpt().ifPresent(valorHora -> dto.setValorHora(Optional.of(valorHora.toString())));
		
		return dto;
	}

	private void validarDadosExistentes(CadastroPFDto cadastroPFDto, BindingResult result) {
		Optional<Empresa> emp = this.empresaService.buscarPorCnpj(cadastroPFDto.getCnpj());
		if(!emp.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa não cadastrada"));
		}
		
		this.funcionarioService.buscarPorCpf(cadastroPFDto.getCpf())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
		
		this.funcionarioService.buscarPorEmail(cadastroPFDto.getEmail())
			.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente")));
		
	}
}
