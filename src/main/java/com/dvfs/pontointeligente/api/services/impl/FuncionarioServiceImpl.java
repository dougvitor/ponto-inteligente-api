package com.dvfs.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dvfs.pontointeligente.api.entities.Funcionario;
import com.dvfs.pontointeligente.api.repositories.FuncionarioRepository;
import com.dvfs.pontointeligente.api.services.FuncionarioService;

@Service
public class FuncionarioServiceImpl implements FuncionarioService{
	
	private static final Logger LOG = LoggerFactory.getLogger(FuncionarioServiceImpl.class);

	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Override
	public Funcionario persistir(Funcionario funcionario) {
		LOG.info("Persistindo funcionario: {}", funcionario);
		return this.funcionarioRepository.save(funcionario);
	}

	@Override
	public Optional<Funcionario> buscarPorCpf(String cpf) {
		LOG.info("Buscando funcionário pelo CPF {}", cpf);
		return Optional.ofNullable(this.funcionarioRepository.findByCpf(cpf));
	}

	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		LOG.info("Buscando funcionário pelo Email {}", email);
		return Optional.ofNullable(this.funcionarioRepository.findByEmail(email));
	}

	@Override
	public Optional<Funcionario> buscarPorId(Long id) {
		LOG.info("Buscando funcionário pelo ID {}", id);
		return this.funcionarioRepository.findById(id);
	}

}
