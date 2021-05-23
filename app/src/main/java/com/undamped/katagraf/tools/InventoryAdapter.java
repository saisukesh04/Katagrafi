package com.undamped.katagraf.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.undamped.katagraf.R;
import com.undamped.katagraf.database.ItemDatabase;
import com.undamped.katagraf.models.Item;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public InventoryAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.expiryText.setText(item.getDateOfExpiry());
        holder.main_item_card.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        ItemDatabase.getInstance(context).ItemDao().deleteItem(item.getPrimary_key());
                        itemList.remove(item);
                        notifyDataSetChanged();
                    }).setNegativeButton("No", (dialogInterface, i) ->
                        Toast.makeText(context, "Please be sure from next time :)", Toast.LENGTH_LONG).show()
            ).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName, itemQuantity, expiryText;
        private ConstraintLayout main_item_card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            expiryText = itemView.findViewById(R.id.expiryText);
            main_item_card = itemView.findViewById(R.id.main_item_card);
        }
    }
}
