package com.example.parcial1am;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

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
        EditText userNameEditText = findViewById(R.id.userNameEditText);
        EditText userPhoneEditText = findViewById(R.id.userPhoneEditText);
        EditText userAddressEditText = findViewById(R.id.userAddressEditText);
        TextView saveProfileButton = findViewById(R.id.saveProfileButton);

        View navShop = findViewById(R.id.navShop);
        View navExplore = findViewById(R.id.navExplore);
        View navCart = findViewById(R.id.navCart);
        View navFavorites = findViewById(R.id.navFavorites);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            loadUserProfile(currentUser, userEmailText, userNameEditText, userPhoneEditText, userAddressEditText);

            saveProfileButton.setOnClickListener(v ->
                    saveUserProfile(currentUser, userNameEditText, userPhoneEditText, userAddressEditText)
            );
        } else {
            userEmailText.setText(R.string.account_no_user);
            userNameEditText.setEnabled(false);
            userPhoneEditText.setEnabled(false);
            userAddressEditText.setEnabled(false);
            saveProfileButton.setEnabled(false);
            saveProfileButton.setAlpha(0.5f);
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
            EditText userNameEditText,
            EditText userPhoneEditText,
            EditText userAddressEditText
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

                        userNameEditText.setText(getEditableValue(name));
                        userPhoneEditText.setText(getEditableValue(phone));
                        userAddressEditText.setText(getEditableValue(address));
                    } else {
                        if (currentUser.getEmail() != null) {
                            userEmailText.setText(currentUser.getEmail());
                        } else {
                            userEmailText.setText(R.string.account_no_user);
                        }

                        userNameEditText.setText("");
                        userPhoneEditText.setText("");
                        userAddressEditText.setText("");

                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error -> {
                    if (currentUser.getEmail() != null) {
                        userEmailText.setText(currentUser.getEmail());
                    } else {
                        userEmailText.setText(R.string.account_no_user);
                    }

                    userNameEditText.setText("");
                    userPhoneEditText.setText("");
                    userAddressEditText.setText("");

                    Toast.makeText(this, "No se pudieron cargar los datos del usuario", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserProfile(
            FirebaseUser currentUser,
            EditText userNameEditText,
            EditText userPhoneEditText,
            EditText userAddressEditText
    ) {
        String name = userNameEditText.getText().toString().trim();
        String phone = userPhoneEditText.getText().toString().trim();
        String address = userAddressEditText.getText().toString().trim();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("phone", phone);
        userData.put("address", address);
        userData.put("email", currentUser.getEmail());

        firestore.collection("users")
                .document(currentUser.getUid())
                .update(userData)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(error ->
                        Toast.makeText(this, "No se pudieron actualizar los datos", Toast.LENGTH_SHORT).show()
                );
    }

    private String getEditableValue(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}