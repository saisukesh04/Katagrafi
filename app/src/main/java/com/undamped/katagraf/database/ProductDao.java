package com.undamped.katagraf.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.undamped.katagraf.models.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    void insertProduct(Product product);

    @Query("DELETE FROM productDb")
    void clearProductDb();

    @Query("SELECT * FROM productDb")
    List<Product> loadAllProducts();

    @Query("SELECT COUNT(*) FROM productDb")
    int countProducts();

    @Query("SELECT * FROM productDb WHERE barCode = :barcode")
    Product fetchProduct(String barcode);

    @Query("SELECT name FROM productDb WHERE barCode = :barcode")
    String fetchProdName(String barcode);

    @Query("SELECT bestBefore FROM productDb WHERE barCode = :barcode")
    int fetchBestBefore(String barcode);
}
