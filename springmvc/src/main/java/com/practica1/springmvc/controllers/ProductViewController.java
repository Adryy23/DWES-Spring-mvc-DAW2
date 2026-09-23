package com.practica1.springmvc.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.practica1.springmvc.models.Product;
import com.practica1.springmvc.models.ProductDatabase;

// Controlador MVC para renderizar vistas HTML con Thymeleaf
@Controller
@RequestMapping("/products")
public class ProductViewController {

    /**
     * Método que se ejecuta antes de cada petición en este controlador.
     * Agrega automáticamente atributos comunes al modelo para que
     * todas las vistas tengan acceso sin repetir código.
     */
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("title", "Gestión de Productos");
        model.addAttribute("totalProducts", ProductDatabase.products.size());
    }

    /**
     * Endpoint que renderiza la vista "list" con la tabla de productos.
     * URL: /products/list
     */
    @GetMapping({"/list", ""})
    public String list(Model model) {
        // Agregamos la lista de productos al modelo
        model.addAttribute("products", ProductDatabase.products);

        // Retorna el nombre de la plantilla Thymeleaf (list.html)
        return "list";
    }

    /**
     * Endpoint que renderiza la vista "details" con el detalle de un producto.
     * URL: /products/details/1
     */
    @GetMapping("/details/{id}")
    public String details(@PathVariable String id, Model model) {
        try {
            // Buscamos el producto; si el id no es número saltará NumberFormatException
            Product product = findProduct(parseId(id));
            model.addAttribute("product", product);

            // Retorna el nombre de la plantilla Thymeleaf (details.html)
            return "details";
        } catch (NumberFormatException e) {
            // Si el id no es un número (ej. /products/details/abc) → error 400
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
        }
    }

    /**
     * Endpoint que muestra el formulario para añadir un producto nuevo.
     * URL: /products/new
     */
    @GetMapping("/new")
    public String newProduct(Model model) {
        // Pasamos un producto vacío para vincularlo al formulario
        model.addAttribute("product", new Product());

        // Retorna el nombre de la plantilla Thymeleaf (form.html)
        return "form";
    }

    /**
     * Endpoint que muestra el formulario para editar un producto existente.
     * URL: /products/edit/1
     */
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable String id, Model model) {
        try {
            model.addAttribute("product", findProduct(parseId(id)));
            return "form";
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
        }
    }

    /**
     * Endpoint que guarda el producto y redirige a la lista.
     * URL: /products/save
     * La validación se hace a mano: nombre no vacío y precio mayor que 0.
     */
    @PostMapping("/save")
    public String save(@ModelAttribute("product") Product product, Model model) {
        // Lista donde iremos guardando los mensajes de error de validación
        List<String> errors = new ArrayList<>();

        // Validación manual del nombre: no puede estar vacío
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("El nombre no puede estar vacío");
        }

        // Validación manual del precio con try/catch
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
            return "form";
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
            model.addAttribute("errors", List.of("Error al guardar el producto: " + e.getMessage()));
            return "form";
        }

        // Redirige a la lista de productos
        return "redirect:/products/list";
    }

    /**
     * Endpoint que elimina un producto y redirige a la lista.
     * URL: /products/delete/1
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        try {
            ProductDatabase.products.remove(findProduct(parseId(id)));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser un número");
        }
        return "redirect:/products/list";
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
