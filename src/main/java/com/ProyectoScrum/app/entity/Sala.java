package com.ProyectoScrum.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "salas")
public class Sala {
	@Id
	private String id;
	private String nombre;
	private int capacidad;
	private String tipo; // Tipo de sala (Ej: VIP, General)
	private String imagenUrl; // URL o ruta de la imagen de la sala
	private EstadoSala estado; // Estado de disponibilidad de la sala

	// Enum para definir los posibles estados de la sala
	public enum EstadoSala {
		Disponible, Ocupado, Mantenimiento
	}

	// Constructor vacío
	public Sala() {
	}

	// Constructor con parámetros
	public Sala(String nombre, int capacidad, String tipo, String imagenUrl, EstadoSala estado) {
		this.nombre = nombre;
		this.capacidad = capacidad;
		this.tipo = tipo;
		this.imagenUrl = imagenUrl;
		this.estado = estado;
	}

	// Getter y Setter para 'id'
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// Getter y Setter para 'nombre'
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	// Getter y Setter para 'capacidad'
	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	// Getter y Setter para 'tipo'
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	// Getter y Setter para 'imagenUrl'
	public String getImagenUrl() {
		return imagenUrl;
	}

	public void setImagenUrl(String imagenUrl) {
		this.imagenUrl = imagenUrl;
	}

	// Getter y Setter para 'estado'
	public EstadoSala getEstado() {
		return estado;
	}

	public void setEstado(EstadoSala estado) {
		this.estado = estado;
	}
}
