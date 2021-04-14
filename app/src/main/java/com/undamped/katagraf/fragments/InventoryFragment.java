package com.undamped.katagraf.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.undamped.katagraf.R;

import butterknife.ButterKnife;

public class InventoryFragment extends Fragment {

    public InventoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);

        ButterKnife.bind(this, root);

        return root;
    }
}