package com.undamped.katagraf.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.undamped.katagraf.tools.DataConverter;

import java.util.List;

@Entity(tableName = "productDb")
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int primary_key;

    private long barCode;
    private String name;
    private int bestBefore;
    private List<String> ingredients;

    public Product() {
    }

    @Ignore
    public Product(long barCode, String name, int bestBefore, List<String> ingredients) {
        this.barCode = barCode;
        this.name = name;
        this.bestBefore = bestBefore;
        this.ingredients = ingredients;
    }

    public int getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(int primary_key) {
        this.primary_key = primary_key;
    }

    public long getBarCode() {
        return barCode;
    }

    public void setBarCode(long barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(int bestBefore) {
        this.bestBefore = bestBefore;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    @TypeConverters(DataConverter.class)
    public String getIngredientString() {
        String text = "";
        for (String ing : ingredients)
            text = text + ing + ",";
        return text;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}