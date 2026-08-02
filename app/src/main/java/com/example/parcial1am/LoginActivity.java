package com.example.parcial1am;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            goToExplore();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        ImageButton passwordVisibilityButton = findViewById(R.id.passwordVisibilityButton);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
        TextView signUpText = findViewById(R.id.signUpText);

        passwordVisibilityButton.setOnClickListener(v -> {
            int cursorPosition = passwordEditText.getSelectionStart();

            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }

            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(Math.max(cursorPosition, 0));
        });

        forgotPasswordText.setOnClickListener(v ->
                Toast.makeText(this, "Recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show()
        );

        signUpText.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateCredentials(email, password)) {
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                createUserDocument(user);
                            } else {
                                Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                                goToExplore();
                            }
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "El correo ya está registrado. Iniciá sesión.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No se pudo crear la cuenta", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateCredentials(email, password)) {
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            goToExplore();
                        } else {
                            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private boolean validateCredentials(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresá tu correo y contraseña", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createUserDocument(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("name", "");
        userData.put("phone", "");
        userData.put("address", "");
        userData.put("createdAt", System.currentTimeMillis());

        firestore.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                    goToExplore();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(this, "Cuenta creada, pero no se pudieron guardar los datos", Toast.LENGTH_SHORT).show();
                    goToExplore();
                });
    }

    private void goToExplore() {
        Intent intent = new Intent(LoginActivity.this, ExploreActivity.class);
        startActivity(intent);
        finish();
    }
}