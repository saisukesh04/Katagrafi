package com.undamped.katagraf.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.undamped.katagraf.tools.DataConverter;

import java.util.List;

@Entity(tableName = "itemDb")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private long primary_key;

    private String name, dateOfExpiry;
    private int quantity;
    private List<String> ingredients;

    public Item() {
    }

    @Ignore
    public Item(String name, String dateOfExpiry, int quantity, List<String> ingredients) {
        this.name = name;
        this.dateOfExpiry = dateOfExpiry;
        this.quantity = quantity;
        this.ingredients = ingredients;
    }

    public long getPrimary_key() {
        return primary_key;
    }

    public void setPrimary_key(long primary_key) {
        this.primary_key = primary_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @TypeConverters(DataConverter.class)
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
