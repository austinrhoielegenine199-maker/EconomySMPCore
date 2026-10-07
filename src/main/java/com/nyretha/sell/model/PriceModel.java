package com.nyretha.sell.model;

public class PriceModel {
    private final String material;
    private final double price;
    private final String category;

    public PriceModel(String material, double price, String category) {
        this.material = material;
        this.price = price;
        this.category = category;
    }

    public String getMaterial() { return material; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
}
