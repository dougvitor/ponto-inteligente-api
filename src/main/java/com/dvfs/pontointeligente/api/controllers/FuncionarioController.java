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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dvfs.pontointeligente.api.dtos.FuncionarioDto;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.response.Response;
import com.dvfs.pontointeligente.api.services.FuncionarioService;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin("*")
public class FuncionarioController {
	
	private static Logger LOG = LoggerFactory.getLogger(FuncionarioController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	public FuncionarioController() {
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<FuncionarioDto>> atualizar(@PathVariable("id") Long id, 
				@Valid @RequestBody FuncionarioDto funcionarioDto, BindingResult result) throws NoSuchAlgorithmException{
		
		LOG.info("Atualizando funcionario: {}", funcionarioDto.toString());
		
		Response<FuncionarioDto> response = new Response<>();
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(id);
		
		if(!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario não encontrado."));
		}
		
		this.atualizarDadosFuncionarios(funcionario.get(), funcionarioDto, result);
		
		if(result.hasErrors()) {
			LOG.error("Erro validando funcionario: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(this.parseFuncionarioDto(this.funcionarioService.persistir(funcionario.get())));
		
		return ResponseEntity.ok(response);
	}

	private void atualizarDadosFuncionarios(Funcionario funcionario, FuncionarioDto dto, BindingResult result)throws NoSuchAlgorithmException {
		
		funcionario.setNome(dto.getNome());
		
		if(!funcionario.getEmail().equals(dto.getEmail())) {
			this.funcionarioService.buscarPorEmail(dto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("email", "Email já existente.")));
			funcionario.setEmail(dto.getEmail());
		}
		
		funcionario.setQtdHorasAlmoco(null);
		dto.getQtdHorasAlmoco().ifPresent(horasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(horasAlmoco)));
		
		funcionario.setQtdHorasTrabalhoDia(null);
		dto.getQtdHorasTrabalhoDia().ifPresent(horasTrabalhadas -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(horasTrabalhadas)));
		
		funcionario.setValorHora(null);
		dto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
	}
	
	private FuncionarioDto parseFuncionarioDto(Funcionario funcionario) {
		FuncionarioDto dto = new FuncionarioDto();
		dto.setId(funcionario.getId());
		dto.setEmail(funcionario.getEmail());
		dto.setNome(funcionario.getNome());
		funcionario.getQtdHorasAlmocoOpt().ifPresent(horasAlmoco -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(horasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt().ifPresent(horasTrabalhadas -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(horasTrabalhadas))));
		funcionario.getValorHoraOpt().ifPresent(valorHora -> dto.setValorHora(Optional.of(valorHora.toString())));
		
		return dto;
	}

}
