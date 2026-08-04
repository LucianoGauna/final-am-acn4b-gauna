package com.example.parcial1am;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "productId";
    public static final String EXTRA_PRODUCT_NAME = "productName";
    public static final String EXTRA_PRODUCT_UNIT = "productUnit";
    public static final String EXTRA_PRODUCT_PRICE = "productPrice";
    public static final String EXTRA_PRODUCT_DESCRIPTION = "productDescription";
    public static final String EXTRA_PRODUCT_IMAGE = "productImage";
    public static final String EXTRA_PRODUCT_IMAGE_NAME = "productImageName";

    private int quantity = 1;
    private double unitPrice = 0;
    private boolean isFavorite = false;

    private TextView quantityText;
    private TextView productPriceText;
    private TextView favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView productImage = findViewById(R.id.productImage);
        TextView productNameText = findViewById(R.id.productNameText);
        TextView productUnitText = findViewById(R.id.productUnitText);
        TextView productDescriptionText = findViewById(R.id.productDescriptionText);
        TextView minusButton = findViewById(R.id.minusButton);
        TextView plusButton = findViewById(R.id.plusButton);
        TextView addToCartButton = findViewById(R.id.addToCartButton);
        TextView backButton = findViewById(R.id.backButton);
        TextView shareButton = findViewById(R.id.shareButton);

        quantityText = findViewById(R.id.quantityText);
        productPriceText = findViewById(R.id.productPriceText);
        favoriteButton = findViewById(R.id.favoriteButton);

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        String productUnit = getIntent().getStringExtra(EXTRA_PRODUCT_UNIT);
        String productDescription = getIntent().getStringExtra(EXTRA_PRODUCT_DESCRIPTION);
        String productImageName = getIntent().getStringExtra(EXTRA_PRODUCT_IMAGE_NAME);
        int productImageResId = getIntent().getIntExtra(EXTRA_PRODUCT_IMAGE, R.drawable.img_coca_cola);

        unitPrice = getIntent().getDoubleExtra(EXTRA_PRODUCT_PRICE, 0);

        productNameText.setText(productName);
        productUnitText.setText(productUnit);
        productDescriptionText.setText(productDescription);
        productImage.setImageResource(productImageResId);

        updatePrice();

        backButton.setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v ->
                Toast.makeText(this, "Compartir próximamente", Toast.LENGTH_SHORT).show()
        );

        favoriteButton.setOnClickListener(v -> toggleFavorite());

        plusButton.setOnClickListener(v -> {
            quantity++;
            updatePrice();
        });

        minusButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updatePrice();
            }
        });

        addToCartButton.setOnClickListener(v -> {
            Product product = new Product(
                    productId,
                    productName,
                    productUnit,
                    unitPrice,
                    productDescription,
                    "bebidas",
                    productImageName
            );

            CartManager.addProduct(product, quantity);

            Toast.makeText(this, R.string.product_added_to_cart, Toast.LENGTH_SHORT).show();
        });
    }

    private void updatePrice() {
        quantityText.setText(String.valueOf(quantity));
        double totalPrice = unitPrice * quantity;
        productPriceText.setText(String.format(Locale.US, "$%.2f", totalPrice));
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            favoriteButton.setText("♥");
            favoriteButton.setTextColor(getColor(R.color.primary_green));
            Toast.makeText(this, R.string.product_favorite_added, Toast.LENGTH_SHORT).show();
        } else {
            favoriteButton.setText("♡");
            favoriteButton.setTextColor(getColor(R.color.text_secondary));
            Toast.makeText(this, R.string.product_favorite_removed, Toast.LENGTH_SHORT).show();
        }
    }
}