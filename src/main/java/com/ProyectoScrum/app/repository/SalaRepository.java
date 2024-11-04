package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Sala;

public interface SalaRepository extends MongoRepository<Sala, String> {
}
