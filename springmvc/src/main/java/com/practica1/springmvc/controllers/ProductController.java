package com.practica1.springmvc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.practica1.springmvc.models.Product;
import com.practica1.springmvc.models.ProductDatabase;

// Controlador MVC para renderizar vistas HTML con Thymeleaf
@Controller
@RequestMapping("/products")
public class ProductController {

    // GET /products/list 
    // Muestra la lista de productos 
    @GetMapping("/list") 
    public String list(Model model) {
        model.addAttribute("products", ProductDatabase.products); 
        return "list"; 
    }

    // GET /products/details/{id} 
    // Muestra los detalles de un producto
    @GetMapping("/details/{id}") 
    public String details(@PathVariable Long id, Model model) { 
        Product product = ProductDatabase.findById(id); 
        if (product == null) { 
            return "redirect:/products/list"; 
        } model.addAttribute("product", product); 
        return "details"; 
    } 

    // GET /products/new
    // Muestra el formulario para añadir un producto
    @GetMapping("/new")
    public String newProduct(Model model) {

    model.addAttribute("product", new Product());

    return "new";
    }

    // POST /products/save
    // Guarda un producto nuevo
    @PostMapping("/save")
    public String save(@ModelAttribute Product product, Model model) {

    // Comprobamos que el nombre no esté vacío
    if (product.getName() == null || product.getName().trim().isEmpty()) {

        model.addAttribute("error", "El nombre no puede estar vacío");

        return "new";
    }

    // Comprobamos que el precio sea mayor que 0
    if (product.getPrice() == null || product.getPrice() <= 0) {

        model.addAttribute("error", "El precio debe ser mayor que 0");

        return "new";
    }

    // Asignamos un id al producto
    product.setId(ProductDatabase.nextId);

    // Aumentamos el siguiente id
    ProductDatabase.nextId++;

    // Añadimos el producto a la lista
    ProductDatabase.products.add(product);

    // Volvemos a la lista
    return "redirect:list";
}
}


