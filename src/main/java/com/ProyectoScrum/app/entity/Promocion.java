package com.ProyectoScrum.app.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "promociones")
public class Promocion {

	@Id
	private String id; // ID de la promoción
	private int descuento; // Descuento aplicado
	private List<String> bebidasIds; // Lista de IDs de bebidas aplicables
	private Boolean activo; // Estado de la promoción (activo/inactivo)

	// Getters y Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDescuento() {
		return descuento;
	}

	public void setDescuento(int descuento) {
		this.descuento = descuento;
	}

	public List<String> getBebidasIds() {
		return bebidasIds;
	}

	public void setBebidasIds(List<String> bebidasIds) {
		this.bebidasIds = bebidasIds;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
