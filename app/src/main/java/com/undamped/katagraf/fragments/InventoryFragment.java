package com.undamped.katagraf.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.undamped.katagraf.R;
import com.undamped.katagraf.database.ItemDao;
import com.undamped.katagraf.database.ItemDatabase;
import com.undamped.katagraf.models.Item;
import com.undamped.katagraf.tools.InventoryAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryFragment extends Fragment {

    @BindView(R.id.inventory_recyclerview) RecyclerView inventory_recyclerview;
    @BindView(R.id.empty_inv_text) TextView empty_inv_text;

    public InventoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);

        ButterKnife.bind(this, root);

        ItemDao itemDao = ItemDatabase.getInstance(getContext()).ItemDao();
        List<Item> itemList = itemDao.loadAllItems();

        inventory_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        inventory_recyclerview.setAdapter(new InventoryAdapter(itemList));

        if (itemList.size() == 0)
            empty_inv_text.setVisibility(View.VISIBLE);

        return root;
    }
}