package com.example.parcial1am;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CartAdapter cartAdapter;
    private TextView emptyCartMessage;
    private TextView checkoutSummaryText;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        emptyCartMessage = findViewById(R.id.emptyCartMessage);
        checkoutSummaryText = findViewById(R.id.checkoutSummaryText);
        checkoutButton = findViewById(R.id.checkoutButton);

        View navShop = findViewById(R.id.navShop);
        View navExplore = findViewById(R.id.navExplore);
        View navFavorites = findViewById(R.id.navFavorites);
        View navAccount = findViewById(R.id.navAccount);

        cartAdapter = new CartAdapter(CartManager.getItems(), this::updateCartState);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        navExplore.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExploreActivity.class);
            startActivity(intent);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        });

        navShop.setOnClickListener(v ->
                Toast.makeText(this, "Tienda próximamente", Toast.LENGTH_SHORT).show()
        );

        navFavorites.setOnClickListener(v ->
                Toast.makeText(this, "Favoritos próximamente", Toast.LENGTH_SHORT).show()
        );

        checkoutButton.setOnClickListener(v -> {
            double cartTotal = CartManager.getTotal();

            checkoutSummaryText.setText(
                    "Resumen del pedido\nTotal estimado: $" +
                            String.format(Locale.US, "%.2f", cartTotal)
            );

            checkoutSummaryText.setVisibility(View.VISIBLE);
        });

        updateCartState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }

        updateCartState();
    }

    private void updateCartState() {
        boolean isCartEmpty = CartManager.isEmpty();

        if (isCartEmpty) {
            emptyCartMessage.setVisibility(View.VISIBLE);
            checkoutSummaryText.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
            checkoutButton.setAlpha(0.5f);
        } else {
            emptyCartMessage.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
            checkoutButton.setAlpha(1f);
        }
    }
}