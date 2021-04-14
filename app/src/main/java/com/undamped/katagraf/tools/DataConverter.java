package com.undamped.katagraf.tools;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataConverter {

    @TypeConverter
    public String fromIngredientList(List<String> ingredients) {
        if (ingredients == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(ingredients, type);
    }

    @TypeConverter
    public List<String> toIngredientList(String ingredientString) {
        if (ingredientString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(ingredientString, type);
    }
}
