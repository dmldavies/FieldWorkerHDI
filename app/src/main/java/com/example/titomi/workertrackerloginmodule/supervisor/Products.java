package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;

/**
 * Created by NeonTetras on 02-Mar-18.
 */

public class Products extends Entity implements Serializable {

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public float price;
    public String category;
    public String description;
    public String photo;
    public double weight;

    protected static final long serialVersionUID = 1l;
}
