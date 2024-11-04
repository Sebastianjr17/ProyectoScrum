package com.ProyectoScrum.app.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "salas")
public class Sala {
    @Id
    private String id;
    private String nombre;
    private int capacidad;
    private String tipo; // Tipo de sala (Ej: VIP, General)
    private List<String> eventosIds; // Lista de IDs de eventos realizados en esta sala

    // Getter y Setter para 'id'
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<String> getEventosIds() {
        return eventosIds;
    }

    public void setEventosIds(List<String> eventosIds) {
        this.eventosIds = eventosIds;
    }
}
