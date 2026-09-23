package com.practica1.springmvc.model;

/**
 * Clase Product: representa un producto con id, nombre, precio y descripción.
 * Es el modelo de datos de la aplicación.
 */
public class Product {

	/** Identificador único del producto (se genera automáticamente). */
	private Long id;

	/** Nombre del producto. */
	private String name;

	/** Precio del producto. */
	private Double price;

	/** Descripción del producto. */
	private String description;

	/** Constructor vacío (necesario para Spring y los formularios). */
	public Product() {
	}

	/** Constructor con todos los datos (sin id). */
	public Product(String name, Double price, String description) {
		this.name = name;
		this.price = price;
		this.description = description;
	}

	// ---------- Getters y Setters ----------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description + "]";
	}
}
