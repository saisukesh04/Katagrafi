package com.undamped.katagraf.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.undamped.katagraf.models.Product;
import com.undamped.katagraf.tools.DataConverter;

@Database(entities = {Product.class}, version = 1, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class ProductDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "productDb";
    private static ProductDatabase sInstance;

    public static ProductDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    ProductDatabase.class, ProductDatabase.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return sInstance;
    }

    public abstract ProductDao ProductDao();
}