package com.undamped.katagraf.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.undamped.katagraf.models.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    void insertItem(Item item);

    @Query("SELECT * FROM itemDb ORDER BY dateOfExpiry")
    List<Item> loadAllItems();

    @Query("DELETE FROM itemDb WHERE primary_key = :primary")
    void deleteItem(long primary);
}