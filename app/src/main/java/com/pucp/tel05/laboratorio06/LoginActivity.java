package com.pucp.tel05.laboratorio06;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import com.pucp.tel05.laboratorio06.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private boolean isRegistroMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> {
            if (isRegistroMode) {
                registerUser();
            } else {
                loginUser();
            }
        });

        binding.btnToggleRegistro.setOnClickListener(v -> toggleRegistroMode());
    }

    private void toggleRegistroMode() {
        isRegistroMode = !isRegistroMode;
        if (isRegistroMode) {
            // Cambiar a modo Registro
            binding.tvTitle.setText("Crear cuenta");
            binding.btnLogin.setText("Registrarse");
            binding.etPasswordConfirm.setVisibility(View.VISIBLE);
            binding.btnToggleRegistro.setText("¿Ya tienes cuenta? Inicia sesión");
        } else {
            // Volver a modo Login
            binding.tvTitle.setText("Iniciar sesión");
            binding.btnLogin.setText("Iniciar sesión");
            binding.etPasswordConfirm.setVisibility(View.GONE);
            binding.btnToggleRegistro.setText("¿Eres nuevo? Regístrate");
        }
        // Limpiar campos al cambiar de modo
        binding.etEmail.setText("");
        binding.etPassword.setText("");
        binding.etPasswordConfirm.setText("");
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        String passConfirm = binding.etPasswordConfirm.getText().toString().trim();

        if (!validateInputs(email, pass)) return;
        if (!validateRegistroInputs(pass, passConfirm)) return;

        setLoading(true);
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        showToast("Cuenta creada exitosamente. Bienvenido!");
                        goToMain();
                    } else {
                        showToast(getFirebaseMessage(task.getException() != null ? task.getException().getMessage() : null, "Registro fallido"));
                    }
                });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();
        if (!validateInputs(email, pass)) return;

        setLoading(true);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        showToast("Sesión iniciada");
                        goToMain();
                    } else {
                        showToast(getFirebaseMessage(task.getException() != null ? task.getException().getMessage() : null, "Credenciales inválidas"));
                    }
                });
    }

    private boolean validateInputs(String email, String pass) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            showToast("Completa los campos");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Correo electrónico inválido");
            return false;
        }
        if (pass.length() < 6) {
            showToast("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        return true;
    }

    private boolean validateRegistroInputs(String pass, String passConfirm) {
        if (!pass.equals(passConfirm)) {
            showToast("Las contraseñas no coinciden");
            return false;
        }
        return true;
    }

    private void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnToggleRegistro.setEnabled(!loading);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getFirebaseMessage(String exceptionMessage, String fallback) {
        if (exceptionMessage == null || exceptionMessage.isEmpty()) return fallback;
        // Simplify common Firebase messages for users
        if (exceptionMessage.contains("The email address is already in use")) return "El correo ya está registrado";
        if (exceptionMessage.contains("The email address is badly formatted")) return "Formato de correo inválido";
        if (exceptionMessage.contains("The password is invalid") || exceptionMessage.contains("password is invalid")) return "Contraseña inválida";
        if (exceptionMessage.contains("too many requests")) return "Demasiados intentos. Intenta más tarde";
        return fallback;
    }
}
