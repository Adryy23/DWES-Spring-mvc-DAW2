package com.practica1.springmvc.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que simula la base de datos en memoria de productos.
 * Al usar atributos static, la lista es la MISMA para toda la aplicación,
 * así los controladores REST y MVC comparten los mismos productos.
 */
public class ProductDatabase {

    // Lista única de productos compartida por toda la aplicación
    public static final List<Product> products = new ArrayList<>();

    // Contador para asignar ids automáticos a los productos nuevos
    public static Long nextId = 1L;

    /**
     * Busca un producto por su id.
     * Devuelve null si no existe.
     */
    public static Product findById(Long id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
}
