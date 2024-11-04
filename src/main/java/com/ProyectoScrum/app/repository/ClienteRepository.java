package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Cliente;

public interface ClienteRepository extends MongoRepository<Cliente, String> {
}
