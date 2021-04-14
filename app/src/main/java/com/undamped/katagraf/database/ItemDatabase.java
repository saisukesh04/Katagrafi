package com.undamped.katagraf.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.undamped.katagraf.models.Item;
import com.undamped.katagraf.tools.DataConverter;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class ItemDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "itemDb";
    private static ItemDatabase sInstance;

    public static ItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    ItemDatabase.class, ItemDatabase.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return sInstance;
    }

    public abstract ItemDao ItemDao();
}
