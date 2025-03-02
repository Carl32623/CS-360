package com.example.lalonde_inventoryapp;

/**
 * Constructor class for an inventory item.
 *
 * @author Carl LaLonde
 *
 * Date: 2/23/2025
 */
public class Item {
    private int id;
    private String name;
    private int quantity;
    private String description;

    public Item(int id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public int getQuantity() {
        return quantity;
    }
}