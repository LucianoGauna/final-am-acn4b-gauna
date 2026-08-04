package com.example.parcial1am;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products;
    private final OnProductClickListener productClickListener;

    public ProductAdapter(List<Product> products, OnProductClickListener productClickListener) {
        this.products = products;
        this.productClickListener = productClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.productNameText.setText(product.getName());
        holder.productUnitText.setText(product.getUnit());
        holder.productPriceText.setText(String.format(Locale.US, "$%.2f", product.getPrice()));

        int imageResId = holder.itemView.getContext()
                .getResources()
                .getIdentifier(
                        product.getImageName(),
                        "drawable",
                        holder.itemView.getContext().getPackageName()
                );

        if (imageResId != 0) {
            holder.productImage.setImageResource(imageResId);
        } else {
            holder.productImage.setImageResource(R.drawable.img_coca_cola);
        }

        holder.itemView.setOnClickListener(v -> productClickListener.onProductClick(product));

        holder.addProductButton.setOnClickListener(v ->
                Toast.makeText(
                        holder.itemView.getContext(),
                        R.string.product_added,
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productNameText;
        TextView productUnitText;
        TextView productPriceText;
        TextView addProductButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productNameText = itemView.findViewById(R.id.productNameText);
            productUnitText = itemView.findViewById(R.id.productUnitText);
            productPriceText = itemView.findViewById(R.id.productPriceText);
            addProductButton = itemView.findViewById(R.id.addProductButton);
        }
    }
}