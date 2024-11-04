package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Reserva;

public interface ReservaRepository extends MongoRepository<Reserva, String> {
}
