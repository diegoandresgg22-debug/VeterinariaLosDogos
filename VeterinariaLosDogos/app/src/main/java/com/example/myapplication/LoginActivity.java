package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox chkRemember;
    private FirebaseAuth mAuth;
    private ProgressDialog progress;
    private SharedPrefManager sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvRegister  = findViewById(R.id.tvRegister);
        chkRemember = findViewById(R.id.chkRemember);

        mAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Iniciando sesión...");
        sp = new SharedPrefManager(this);

        if (sp.shouldRemember()) {
            String savedEmail = sp.getEmail();
            if (savedEmail != null) etEmail.setText(savedEmail);
            chkRemember.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()) {
            etPassword.setError("Contraseña requerida");
            etPassword.requestFocus();
            return;
        }

        progress.show();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    progress.dismiss();
                    if (task.isSuccessful()) {
                        sp.saveLogin(email, chkRemember.isChecked());
                        Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Exception ex = task.getException();
                        String code = "";
                        if (ex instanceof com.google.firebase.auth.FirebaseAuthException) {
                            code = ((com.google.firebase.auth.FirebaseAuthException) ex).getErrorCode();
                        }
                        String msg = (ex != null) ? ex.getMessage() : "Fallo desconocido";
                        Toast.makeText(this, "Error de autenticación: " + msg + (code.isEmpty()?"":" ["+code+"]"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}