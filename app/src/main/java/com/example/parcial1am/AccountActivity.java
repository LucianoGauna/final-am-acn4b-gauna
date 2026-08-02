package com.example.parcial1am;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.accountRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        TextView userEmailText = findViewById(R.id.userEmailText);
        TextView logoutButton = findViewById(R.id.logoutButton);
        TextView userNameText = findViewById(R.id.userNameText);
        TextView userPhoneText = findViewById(R.id.userPhoneText);
        TextView userAddressText = findViewById(R.id.userAddressText);

        View navShop = findViewById(R.id.navShop);
        View navExplore = findViewById(R.id.navExplore);
        View navCart = findViewById(R.id.navCart);
        View navFavorites = findViewById(R.id.navFavorites);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            loadUserProfile(currentUser, userEmailText, userNameText, userPhoneText, userAddressText);
        } else {
            userEmailText.setText(R.string.account_no_user);
            userNameText.setText("Nombre: -");
            userPhoneText.setText("Teléfono: -");
            userAddressText.setText("Dirección: -");
        }

        logoutButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        navExplore.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ExploreActivity.class);
            startActivity(intent);
        });

        navCart.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navShop.setOnClickListener(v ->
                Toast.makeText(this, "Tienda próximamente", Toast.LENGTH_SHORT).show()
        );

        navFavorites.setOnClickListener(v ->
                Toast.makeText(this, "Favoritos próximamente", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserProfile(
            FirebaseUser currentUser,
            TextView userEmailText,
            TextView userNameText,
            TextView userPhoneText,
            TextView userAddressText
    ) {
        firestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String address = documentSnapshot.getString("address");

                        if (email != null && !email.isEmpty()) {
                            userEmailText.setText(email);
                        } else if (currentUser.getEmail() != null) {
                            userEmailText.setText(currentUser.getEmail());
                        } else {
                            userEmailText.setText(R.string.account_no_user);
                        }

                        userNameText.setText("Nombre: " + getVisibleValue(name));
                        userPhoneText.setText("Teléfono: " + getVisibleValue(phone));
                        userAddressText.setText("Dirección: " + getVisibleValue(address));
                    } else {
                        if (currentUser.getEmail() != null) {
                            userEmailText.setText(currentUser.getEmail());
                        } else {
                            userEmailText.setText(R.string.account_no_user);
                        }

                        userNameText.setText("Nombre: -");
                        userPhoneText.setText("Teléfono: -");
                        userAddressText.setText("Dirección: -");

                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error -> {
                    if (currentUser.getEmail() != null) {
                        userEmailText.setText(currentUser.getEmail());
                    } else {
                        userEmailText.setText(R.string.account_no_user);
                    }

                    userNameText.setText("Nombre: -");
                    userPhoneText.setText("Teléfono: -");
                    userAddressText.setText("Dirección: -");

                    Toast.makeText(this, "No se pudieron cargar los datos del usuario", Toast.LENGTH_SHORT).show();
                });
    }

    private String getVisibleValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }

        return value;
    }
}