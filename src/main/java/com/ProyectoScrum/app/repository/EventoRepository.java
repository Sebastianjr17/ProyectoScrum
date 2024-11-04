package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Evento;

public interface EventoRepository extends MongoRepository<Evento, String> {
}
