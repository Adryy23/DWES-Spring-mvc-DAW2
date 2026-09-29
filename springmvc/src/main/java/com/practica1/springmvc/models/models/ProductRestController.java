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

        try {

            if (ProductDatabase.products.isEmpty()) {
                return null;
            }

            return ProductDatabase.products;

        } catch (Exception e) {

            return null;
        }
    }

    // GET /api/products/{id}
    // Busca un producto por su id
    @GetMapping("/{id}")
    public Product details(@PathVariable String id) {

        try {

            // Comprobamos que el id sea un número
            Long idNumber = Long.parseLong(id);

            // Buscamos el producto
            Product product = ProductDatabase.findById(idNumber);

            // Comprobamos que el producto exista
            if (product == null) {
                return null;
            }

            // Devolvemos el producto
            return product;

        } catch (NumberFormatException e) {

            return null;
        }
    }

    // POST /api/products
    // Añade un nuevo producto
    @PostMapping
    public Product create(@RequestBody Product product) {

        try {

            // Comprobamos que el producto tenga nombre
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return null;
            }

            // Comprobamos que el producto tenga precio
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return null;
            }

            // Asignamos un id al nuevo producto
            product.setId(ProductDatabase.nextId);

            // Preparamos el siguiente id
            ProductDatabase.nextId++;

            // Añadimos el producto a la lista
            ProductDatabase.products.add(product);

            // Devolvemos el producto creado
            return product;

        } catch (Exception e) {

            return null;
        }
    }

    // PUT /api/products/{id}
    // Modifica un producto existente
    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {

        try {

            // Comprobamos que el id sea un número
            Long idNumber = Long.parseLong(id);

            // Buscamos el producto
            Product producto = ProductDatabase.findById(idNumber);

            // Comprobamos que el producto exista
            if (producto == null) {
                return null;
            }

            // Comprobamos que el nombre no esté vacío
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return null;
            }

            // Comprobamos que el precio sea mayor que 0
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return null;
            }

            // Modificamos los datos del producto
            producto.setName(product.getName());
            producto.setPrice(product.getPrice());
            producto.setDescription(product.getDescription());

            // Devolvemos el producto modificado
            return producto;

        } catch (NumberFormatException e) {

            // El id no es un número
            return null;

        } catch (Exception e) {

            // Cualquier otro error
            return null;
        }
    }

    // DELETE /api/products/{id}
    // Elimina un producto
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {

        try {

            // Comprobamos que el id sea un número
            Long idNumber = Long.parseLong(id);

            // Buscamos el producto
            Product product = ProductDatabase.findById(idNumber);

            // Comprobamos que el producto exista
            if (product == null) {
                return;
            }

            // Eliminamos el producto
            ProductDatabase.products.remove(product);

        } catch (NumberFormatException e) {

            // El id no es un número
            return;

        } catch (Exception e) {

            // Cualquier otro error
            return;
        }
    }

}
