package com.dio.srg.services;

import java.util.List;

import com.dio.srg.domain.Cliente;

public interface ClienteService {

	Cliente findByID(Integer id);

	void insert(Cliente cliente);

	void update(Cliente cliente);

	void delete(Integer id);

	List<Cliente> findAll();

}
