package com.ProyectoScrum.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.ProyectoScrum.app.entity.Reserva;
import java.util.List;

public interface ReservaRepository extends MongoRepository<Reserva, String> {
    // Método personalizado para buscar reservas por correo
    List<Reserva> findByCorreoElectronico(String correo);
}
