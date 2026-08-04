package com.example.parcial1am;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    private final List<CartItem> cartItems;
    private final OnCartChangedListener cartChangedListener;

    public CartAdapter(List<CartItem> cartItems, OnCartChangedListener cartChangedListener) {
        this.cartItems = cartItems;
        this.cartChangedListener = cartChangedListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        holder.cartItemNameText.setText(product.getName());
        holder.cartItemUnitText.setText(product.getUnit());
        holder.cartItemQuantityText.setText(String.valueOf(cartItem.getQuantity()));
        holder.cartItemPriceText.setText(String.format(Locale.US, "$%.2f", cartItem.getTotalPrice()));

        int imageResId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(
                        product.getImageName(),
                        "drawable",
                        holder.itemView.getContext().getPackageName()
                );

        if (imageResId != 0) {
            holder.cartItemImage.setImageResource(imageResId);
        } else {
            holder.cartItemImage.setImageResource(R.drawable.img_coca_cola);
        }

        holder.cartItemPlusButton.setOnClickListener(v -> {
            CartManager.increaseQuantity(product.getId());
            notifyDataSetChanged();
            cartChangedListener.onCartChanged();
        });

        holder.cartItemMinusButton.setOnClickListener(v -> {
            CartManager.decreaseQuantity(product.getId());
            notifyDataSetChanged();
            cartChangedListener.onCartChanged();
        });

        holder.cartItemRemoveButton.setOnClickListener(v -> {
            CartManager.removeProduct(product.getId());
            notifyDataSetChanged();
            cartChangedListener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemNameText;
        TextView cartItemUnitText;
        TextView cartItemQuantityText;
        TextView cartItemPriceText;
        Button cartItemMinusButton;
        Button cartItemPlusButton;
        Button cartItemRemoveButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemImage = itemView.findViewById(R.id.cartItemImage);
            cartItemNameText = itemView.findViewById(R.id.cartItemNameText);
            cartItemUnitText = itemView.findViewById(R.id.cartItemUnitText);
            cartItemQuantityText = itemView.findViewById(R.id.cartItemQuantityText);
            cartItemPriceText = itemView.findViewById(R.id.cartItemPriceText);
            cartItemMinusButton = itemView.findViewById(R.id.cartItemMinusButton);
            cartItemPlusButton = itemView.findViewById(R.id.cartItemPlusButton);
            cartItemRemoveButton = itemView.findViewById(R.id.cartItemRemoveButton);
        }
    }
}