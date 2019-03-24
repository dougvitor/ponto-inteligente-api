package com.dvfs.pontointeligente.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dvfs.pontointeligente.api.dtos.LancamentoDto;
import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.entities.Lancamento;
import com.dvfs.pontointeligente.api.enums.TipoEnum;
import com.dvfs.pontointeligente.api.response.Response;
import com.dvfs.pontointeligente.api.services.FuncionarioService;
import com.dvfs.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin("*")
public class LancamentoController {
	
	private static final Logger LOG = LoggerFactory.getLogger(LancamentoController.class);
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;
	
	public LancamentoController() {
	}
	
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDto>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue =  "id")String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir){
		
		LOG.info("Buscando lançamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
		
		Response<Page<LancamentoDto>> response = new Response<>();
		
		PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		
		Page<LancamentoDto> lancamentosDto	= lancamentos.map(lancamento -> this.parseLancamentoParaDto(lancamento));
		
		response.setData(lancamentosDto);
		
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Response<LancamentoDto>> listarPorId(@PathVariable("id") Long id){
		LOG.info("Buscando lançamento por ID: {}", id);
		
		Response<LancamentoDto> response = new Response<>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()) {
			LOG.info("Lançamento não encontrado para o ID: {}", id);
			response.getErrors().add(messageSource.getMessage("message.lancamento.notfound", new Long[] {id}, LocaleContextHolder.getLocale()));
			return ResponseEntity.notFound().build();
		}
		
		response.setData(this.parseLancamentoParaDto(lancamento.get()));
		return ResponseEntity.ok(response);
	}
	
	@PostMapping
	public ResponseEntity<Response<LancamentoDto>> adicionar(@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException{
		LOG.info("Adicionando lançamento: {}", lancamentoDto.toString());
		
		Response<LancamentoDto> response = new Response<>();
		
		validarFuncionario(lancamentoDto, result);
		
		if(result.hasErrors()) {
			LOG.error("Erro validando lançamento: {}" , result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Lancamento lancamento = this.lancamentoService.persistir(this.parseDtoParaLancamento(lancamentoDto, result));
		response.setData(this.parseLancamentoParaDto(lancamento));
		return ResponseEntity.ok(response);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDto>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException{
		LOG.info("Atualizando lançamento: {}", lancamentoDto.toString());
		
		Response<LancamentoDto> response = new Response<>();
		
		lancamentoDto.setId(Optional.of(id));
		
		validarFuncionario(lancamentoDto, result);
		Lancamento lancamento = this.parseDtoParaLancamento(lancamentoDto, result);
		
		if(result.hasErrors()) {
			LOG.error("Erro validando lançamento: {}" , result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.parseLancamentoParaDto(lancamento));
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<LancamentoDto>> remover(@PathVariable("id") Long id) throws ParseException{
		LOG.info("Removendo lançamento ID: {}", id);
		
		Response<LancamentoDto> response = new Response<>();
		
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);
		
		if(!lancamento.isPresent()) {
			LOG.error("Erro ao remover devido ao lançamento ID: {} ser inválido." , id);
			response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
			return ResponseEntity.notFound().build();
		}
		
		this.lancamentoService.remover(id);
		return ResponseEntity.ok(response);
	}

	private void validarFuncionario(@Valid LancamentoDto lancamentoDto, BindingResult result) {
		if(lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionário não informado."));
			return;
		}
		
		LOG.info("Validando funcionário id {}: ", lancamentoDto.getFuncionarioId());
		
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDto.getFuncionarioId());
		
		if(!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado. ID inexistente"));
		}
	}
	
	private Lancamento parseDtoParaLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();
		
		if(lancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDto.getId().get());
			
			if(lanc.isPresent()) {
				lancamento = lanc.get();
			}else {
				result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
			}
		}else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
		}
		
		lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));
		lancamento.setDescricao(lancamentoDto.getDescricao());
		lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
		
		if(EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
		}else {
			result.addError(new ObjectError("tipo", "Tipo inválido."));
		}
		
		return lancamento;
	}

	private LancamentoDto parseLancamentoParaDto(Lancamento lancamento) {
		LancamentoDto dto = new LancamentoDto();
		dto.setId(Optional.of(lancamento.getId()));
		dto.setData(this.dateFormat.format(lancamento.getData()));
		dto.setTipo(lancamento.getTipo().toString());
		dto.setDescricao(lancamento.getDescricao());
		dto.setLocalizacao(lancamento.getLocalizacao());
		dto.setFuncionarioId(lancamento.getFuncionario().getId());
		return dto;
	}

}
