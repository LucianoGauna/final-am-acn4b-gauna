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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BeveragesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private List<Product> beverageProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beverages);

        firestore = FirebaseFirestore.getInstance();
        beverageProducts = new ArrayList<>();
        loadBeverageProductsFromFirestore();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.beveragesRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View backButton = findViewById(R.id.backButton);
        View filterButton = findViewById(R.id.filterButton);

        View dietCokeCard = findViewById(R.id.dietCokeCard);
        View spriteCard = findViewById(R.id.spriteCard);
        View appleGrapeJuiceCard = findViewById(R.id.appleGrapeJuiceCard);
        View orangeJuiceCard = findViewById(R.id.orangeJuiceCard);
        View cocaColaCard = findViewById(R.id.cocaColaCard);
        View pepsiCard = findViewById(R.id.pepsiCard);

        View addDietCokeButton = findViewById(R.id.addDietCokeButton);
        View addSpriteButton = findViewById(R.id.addSpriteButton);
        View addAppleGrapeJuiceButton = findViewById(R.id.addAppleGrapeJuiceButton);
        View addOrangeJuiceButton = findViewById(R.id.addOrangeJuiceButton);
        View addCocaColaButton = findViewById(R.id.addCocaColaButton);
        View addPepsiButton = findViewById(R.id.addPepsiButton);

        backButton.setOnClickListener(v -> finish());

        filterButton.setOnClickListener(v ->
                Toast.makeText(this, "Filtros próximamente", Toast.LENGTH_SHORT).show()
        );

        dietCokeCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_diet_coke),
                getString(R.string.unit_can_355),
                2500.00,
                getString(R.string.description_diet_coke),
                R.drawable.img_diet_coke
        ));

        spriteCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_sprite),
                getString(R.string.unit_can_325),
                2200.00,
                getString(R.string.description_sprite),
                R.drawable.img_sprite
        ));

        appleGrapeJuiceCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_apple_grape_juice),
                getString(R.string.unit_bottle_2l),
                1800.99,
                getString(R.string.description_apple_grape_juice),
                R.drawable.img_apple_grape_juice
        ));

        orangeJuiceCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_orange_juice),
                getString(R.string.unit_bottle_2l),
                1800.99,
                getString(R.string.description_orange_juice),
                R.drawable.img_orange_juice
        ));

        cocaColaCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_coca_cola),
                getString(R.string.unit_can_355),
                2500.00,
                getString(R.string.description_coca_cola),
                R.drawable.img_coca_cola
        ));

        pepsiCard.setOnClickListener(v -> openProductDetail(
                getString(R.string.product_pepsi),
                getString(R.string.unit_can_355),
                2500.00,
                getString(R.string.description_pepsi),
                R.drawable.img_pepsi
        ));

        addDietCokeButton.setOnClickListener(v -> showProductAdded());
        addSpriteButton.setOnClickListener(v -> showProductAdded());
        addAppleGrapeJuiceButton.setOnClickListener(v -> showProductAdded());
        addOrangeJuiceButton.setOnClickListener(v -> showProductAdded());
        addCocaColaButton.setOnClickListener(v -> showProductAdded());
        addPepsiButton.setOnClickListener(v -> showProductAdded());
    }

    private void showProductAdded() {
        Toast.makeText(this, R.string.product_added, Toast.LENGTH_SHORT).show();
    }

    private void openProductDetail(
            String productName,
            String productUnit,
            double productPrice,
            String productDescription,
            int productImage
    ) {
        Intent intent = new Intent(BeveragesActivity.this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, productName);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_UNIT, productUnit);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, productPrice);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, productDescription);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMAGE, productImage);
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