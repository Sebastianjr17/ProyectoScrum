package com.ProyectoScrum.app.repository;

import com.ProyectoScrum.app.entity.Promocion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocionRepository extends MongoRepository<Promocion, String> {

}
