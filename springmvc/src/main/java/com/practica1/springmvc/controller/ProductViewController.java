package com.practica1.springmvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.practica1.springmvc.model.Product;
import com.practica1.springmvc.model.ProductDatabase;

/**
 * Controlador MVC (patrón MVC) para las vistas Thymeleaf.
 * Usa la base de datos en memoria compartida (ProductDatabase).
 * Con @ModelAttribute se pasan datos comunes a todas las vistas.
 */
@Controller
@RequestMapping("/products")
public class ProductViewController {

	// Base de datos en memoria compartida por toda la aplicación

	/**
	 * @ModelAttribute a nivel de método: se ejecuta antes de cada handler
	 * y añade atributos visibles en TODAS las vistas de este controlador.
	 */
	@ModelAttribute
	public void addCommonAttributes(Model model) {
		model.addAttribute("totalProducts", ProductDatabase.products.size());
		model.addAttribute("appName", "Gestión de Productos");
	}

	/** GET /products/list → muestra la lista en una tabla. */
	@GetMapping({"/list", ""})
	public String list(Model model) {
		model.addAttribute("products", ProductDatabase.products);
		return "products/list";
	}

	/** GET /products/details/{id} → muestra los detalles de un producto. */
	@GetMapping("/details/{id}")
	public String details(@PathVariable String id, Model model) {
		try {
			Product product = findProduct(Long.parseLong(id));
			model.addAttribute("product", product);
			return "products/details";
		} catch (NumberFormatException e) {
			// Si el id no es un número (ej. /products/details/abc) → error 400
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
		}
	}

	/** GET /products/new → muestra un formulario para añadir un producto. */
	@GetMapping("/new")
	public String newProduct(Model model) {
		model.addAttribute("product", new Product());
		return "products/form";
	}

	/** GET /products/edit/{id} → formulario de edición (reutiliza el mismo form). */
	@GetMapping("/edit/{id}")
	public String editProduct(@PathVariable String id, Model model) {
		try {
			model.addAttribute("product", findProduct(Long.parseLong(id)));
			return "products/form";
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
		}
	}

	/**
	 * POST /products/save → guarda el producto y redirige a /products/list.
	 * La validación se hace a mano: nombre no vacío y precio mayor que 0.
	 * Si hay errores se vuelve al formulario mostrando el mensaje.
	 */
	@PostMapping("/save")
	public String save(@ModelAttribute("product") Product product, Model model) {
		// Lista donde iremos guardando los mensajes de error de validación
		java.util.List<String> errors = new java.util.ArrayList<>();

		// Validación manual del nombre: no puede estar vacío
		if (product.getName() == null || product.getName().trim().isEmpty()) {
			errors.add("El nombre no puede estar vacío");
		}

		// Validación manual del precio con try/catch:
		// si escriben texto en el campo numérico o un valor no válido, controlamos el error
		try {
			if (product.getPrice() == null || product.getPrice() <= 0) {
				errors.add("El precio debe ser mayor que 0");
			}
		} catch (Exception e) {
			errors.add("El precio no es un número válido");
		}

		// Si hay errores volvemos al formulario para mostrarlos
		if (!errors.isEmpty()) {
			model.addAttribute("errors", errors);
			return "products/form";
		}

		try {
			if (product.getId() == null) {
				// Producto nuevo: le asignamos id y lo añadimos
				product.setId(ProductDatabase.nextId++);
				ProductDatabase.products.add(product);
			} else {
				// Edición: buscamos el existente y actualizamos sus datos
				Product existing = findProduct(product.getId());
				existing.setName(product.getName());
				existing.setPrice(product.getPrice());
				existing.setDescription(product.getDescription());
			}
		} catch (Exception e) {
			// Cualquier error inesperado al guardar → volvemos al formulario con el mensaje
			model.addAttribute("errors", java.util.List.of("Error al guardar el producto: " + e.getMessage()));
			return "products/form";
		}

		return "redirect:/products/list";
	}

	/** POST /products/delete/{id} → elimina el producto y redirige a la lista. */
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable String id) {
		try {
			ProductDatabase.products.remove(findProduct(Long.parseLong(id)));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
		}
		return "redirect:/products/list";
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
