package com.example.parcial1am;

public class Product {
    private String id;
    private String name;
    private String unit;
    private double price;
    private String description;
    private String category;
    private String imageName;

    public Product(
            String id,
            String name,
            String unit,
            double price,
            String description,
            String category,
            String imageName
    ) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImageName() {
        return imageName;
    }
}