package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Dj;

public interface DjRepository extends MongoRepository<Dj, String> {
}
