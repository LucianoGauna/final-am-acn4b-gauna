package com.example.parcial1am;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BeveragesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private List<Product> beverageProducts;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beverages);

        firestore = FirebaseFirestore.getInstance();
        beverageProducts = new ArrayList<>();

        RecyclerView productsRecyclerView = findViewById(R.id.productsRecyclerView);
        productAdapter = new ProductAdapter(beverageProducts, this::openProductDetail);

        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productsRecyclerView.setAdapter(productAdapter);

        loadBeverageProductsFromFirestore();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.beveragesRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View backButton = findViewById(R.id.backButton);
        View filterButton = findViewById(R.id.filterButton);

        backButton.setOnClickListener(v -> finish());

        filterButton.setOnClickListener(v ->
                Toast.makeText(this, "Filtros próximamente", Toast.LENGTH_SHORT).show()
        );
    }

    private void openProductDetail(Product product) {
        int imageResId = getResources().getIdentifier(
                product.getImageName(),
                "drawable",
                getPackageName()
        );

        if (imageResId == 0) {
            imageResId = R.drawable.img_coca_cola;
        }

        Intent intent = new Intent(BeveragesActivity.this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, product.getName());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_UNIT, product.getUnit());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.getDescription());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMAGE, imageResId);
        startActivity(intent);
    }

    private void loadBeverageProductsFromFirestore() {
        firestore.collection("products")
                .whereEqualTo("category", "bebidas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    beverageProducts.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String unit = document.getString("unit");
                        Double price = document.getDouble("price");
                        String description = document.getString("description");
                        String category = document.getString("category");
                        String imageName = document.getString("imageName");

                        Product product = new Product(
                                id,
                                getSafeString(name),
                                getSafeString(unit),
                                price != null ? price : 0,
                                getSafeString(description),
                                getSafeString(category),
                                getSafeString(imageName)
                        );

                        beverageProducts.add(product);
                    }

                    productAdapter.notifyDataSetChanged();

                    Toast.makeText(
                            this,
                            "Productos cargados desde Firestore: " + beverageProducts.size(),
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(error ->
                        Toast.makeText(
                                this,
                                "No se pudieron cargar los productos",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private String getSafeString(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}