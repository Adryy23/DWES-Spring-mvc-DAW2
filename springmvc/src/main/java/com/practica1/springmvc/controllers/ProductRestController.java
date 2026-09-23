package com.practica1.springmvc.controllers;

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

import com.practica1.springmvc.models.Product;
import com.practica1.springmvc.models.ProductDatabase;

// Controlador REST para exponer endpoints bajo /api/products
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    /**
     * Endpoint que devuelve la lista completa de productos.
     * Ejemplo de URL: /api/products
     */
    @GetMapping
    public List<Product> list() {
        // Devolvemos la lista de la base de datos en memoria
        // Spring la convierte a JSON automáticamente
        return ProductDatabase.products;
    }

    /**
     * Endpoint que devuelve un producto concreto por su id.
     * Ejemplo de URL: /api/products/1
     */
    @GetMapping("/{id}")
    public Product details(@PathVariable String id) {
        return findProduct(parseId(id));
    }

    /**
     * Endpoint para crear un producto a partir de un JSON.
     * Ejemplo de URL: /api/products
     */
    @PostMapping
    public Product create(@RequestBody Product product) {
        // Le asignamos un id automático y lo guardamos en la lista
        product.setId(ProductDatabase.nextId++);
        ProductDatabase.products.add(product);
        return product;
    }

    /**
     * Endpoint para modificar un producto existente.
     * Ejemplo de URL: /api/products/1
     */
    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        // Buscamos el producto existente y actualizamos sus datos
        Product existing = findProduct(parseId(id));
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setDescription(product.getDescription());
        return existing;
    }

    /**
     * Endpoint para eliminar un producto.
     * Ejemplo de URL: /api/products/1
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
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

    /**
     * Método auxiliar: busca un producto por id.
     * Si no existe lanza un error 404.
     */
    private Product findProduct(Long id) {
        Product p = ProductDatabase.findById(id);
        if (p == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se ha encontrado el producto con id " + id);
        }
        return p;
    }
}
