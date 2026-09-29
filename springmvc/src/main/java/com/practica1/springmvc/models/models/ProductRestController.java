package com.practica1.springmvc.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.practica1.springmvc.models.Product;
import com.practica1.springmvc.models.ProductDatabase;

// Controlador REST para exponer endpoints bajo /api/products
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    // GET /api/products
    // Devuelve todos los productos
    @GetMapping
    public List<Product> list() {
        return ProductDatabase.products;
    }

    // GET /api/products/{id}
    // Busca un producto por su id
    @GetMapping("/{id}")
    public Object details(@PathVariable String id) {

        try {
            Long idNumber = Long.parseLong(id);

            Product product = ProductDatabase.findById(idNumber);

            if (product == null) {
                return "Error: no se ha encontrado el producto con id " + id;
            }

            return product;

        } catch (NumberFormatException e) {
            return "Error: el id debe ser un número";
        }
    }

    // POST /api/products
    // Añade un nuevo producto
    @PostMapping
    public Product create(@RequestBody Product product) {

        // Asignamos un id al nuevo producto
        product.setId(ProductDatabase.nextId);

        // Preparamos el siguiente id
        ProductDatabase.nextId++;

        // Añadimos el producto a la lista
        ProductDatabase.products.add(product);

        // Devolvemos el producto creado
        return product;
    }

    // PUT /api/products/{id}
    // Modifica un producto existente
    @PutMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody Product product) {

        try {
            Long idNumber = Long.parseLong(id);

            Product producto = ProductDatabase.findById(idNumber);

            if (producto == null) {
                return "Error: no se ha encontrado el producto con id " + id;
            }

            producto.setName(product.getName());
            producto.setPrice(product.getPrice());
            producto.setDescription(product.getDescription());

            return producto;

        } catch (NumberFormatException e) {
            return "Error: el id debe ser un número";
        }
    }

    // DELETE /api/products/{id}
    // Elimina un producto
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {

        try {
            Long idNumber = Long.parseLong(id);

            Product product = ProductDatabase.findById(idNumber);

            if (product == null) {
                return "Error: no se ha encontrado el producto con id " + id;
            }

            ProductDatabase.products.remove(product);

            return "Producto eliminado correctamente";

        } catch (NumberFormatException e) {
            return "Error: el id debe ser un número";
        }
    }

}
