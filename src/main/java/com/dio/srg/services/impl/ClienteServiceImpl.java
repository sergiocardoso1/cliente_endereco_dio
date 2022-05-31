package com.dio.srg.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.dio.srg.domain.Cliente;
import com.dio.srg.domain.Endereco;
import com.dio.srg.repositories.ClienteRepository;
import com.dio.srg.repositories.EnderecoRepository;
import com.dio.srg.services.ClienteService;
import com.dio.srg.services.ViaCepService;
import com.dio.srg.services.exceptions.DataIntegrityException;
import com.dio.srg.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private ViaCepService viaCepService;

	
	@Override
	public Cliente findByID(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	@Override
	public void insert(Cliente cliente) {
		cliente.setId(null);
		salvarClienteComCep(cliente);
	}

	@Override
	public void update(Cliente cliente) {
		findByID(cliente.getId());
		salvarClienteComCep(cliente);
	}

	@Override
	public void delete(Integer id) {
		findByID(id);
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma cliente que possue enderecos.");

		}
	}

	@Override
	public List<Cliente> findAll() {
		return repository.findAll();	
	}
	
	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		repository.save(cliente);
	}
}
