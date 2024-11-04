package com.ProyectoScrum.app.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promociones")
public class Promocion {
    @Id
    private String id;  // ID de la promoción
    private String nombre;  // Nombre de la promoción
    private String descripcion;  // Descripción de la promoción
    private int descuento;  // Descuento aplicado (ahora es un entero)
    private List<String> eventosIds; // Lista de IDs de eventos aplicables

    // Getter y Setter para id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters y Setters para otros campos
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDescuento() {
        return descuento; // Ahora retorna un int
    }

    public void setDescuento(int descuento) { // Ahora acepta un int
        this.descuento = descuento;
    }

    public List<String> getEventosIds() {
        return eventosIds;
    }

    public void setEventosIds(List<String> eventosIds) {
        this.eventosIds = eventosIds;
    }
}
