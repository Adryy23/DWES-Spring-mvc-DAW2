package com.practica1.springmvc.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.practica1.springmvc.model.Product;
import com.practica1.springmvc.model.ProductDatabase;

/**
 * Controlador REST para la gestión de productos vía API (/api/products).
 * Expone las operaciones CRUD usando anotaciones de Spring MVC.
 * Los productos se guardan en la base de datos en memoria (ProductDatabase).
 */
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

	/** GET /api/products → lista de productos. */
	@GetMapping
	public List<Product> getAllProducts() {
		return ProductDatabase.products;
	}

	/** GET /api/products/{id} → devuelve un producto concreto. */
	@GetMapping("/{id}")
	public Product getProduct(@PathVariable String id) {
		return findProduct(parseId(id));
	}

	/** POST /api/products → agrega un producto (en JSON). */
	@PostMapping
	public Product createProduct(@RequestBody Product product) {
		product.setId(ProductDatabase.nextId++);
		ProductDatabase.products.add(product);
		return product;
	}

	/** PUT /api/products/{id} → modifica un producto. */
	@PutMapping("/{id}")
	public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
		Product existing = findProduct(parseId(id));
		existing.setName(product.getName());
		existing.setPrice(product.getPrice());
		existing.setDescription(product.getDescription());
		return existing;
	}

	/** DELETE /api/products/{id} → elimina un producto. */
	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable String id) {
		Product existing = findProduct(parseId(id));
		ProductDatabase.products.remove(existing);
	}

	/**
	 * Método auxiliar: convierte el id de String a Long con try/catch.
	 * Si no es un número válido lanza un error 400.
	 */
	private Long parseId(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
		}
	}

	/** Método auxiliar: busca un producto por id, o lanza un error 404 si no existe. */
	private Product findProduct(Long id) {
		Product p = ProductDatabase.findById(id);
		if (p == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado el producto con id " + id);
		}
		return p;
	}
}
