package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.models.Pet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPetName;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName      = findViewById(R.id.etName);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        etPetName   = findViewById(R.id.etPetName);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseDatabase.getInstance().getReference();

        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Registrando...");

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name    = etName.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString().trim();
        String petName = etPetName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Nombre requerido");
            etName.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }
        if (pass.isEmpty() || pass.length() < 6) {
            etPassword.setError("Contraseña de al menos 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        progress.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception ex = task.getException();
                        String code = (ex instanceof com.google.firebase.auth.FirebaseAuthException)
                                ? ((com.google.firebase.auth.FirebaseAuthException) ex).getErrorCode() : "";
                        android.util.Log.e("FBCHK", "Auth failed code=" + code + " msg=" + (ex!=null?ex.getMessage():""));
                        Toast.makeText(this, "Registro falló: " + (code.isEmpty() ? "CONFIGURATION" : code),
                                Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        return;
                    }

                    String uid = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;
                    if (uid == null || uid.isEmpty()) {
                        progress.dismiss();
                        Toast.makeText(this, "No se pudo obtener el UID del usuario.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("uid", uid);
                    userData.put("name", name);
                    userData.put("email", email);

                    db.child("users").child(uid).setValue(userData)
                            .addOnCompleteListener(t2 -> {
                                if (!t2.isSuccessful()) {
                                    progress.dismiss();
                                    String msg = (t2.getException() != null) ? t2.getException().getMessage() : "Error desconocido guardando perfil";
                                    Toast.makeText(this, "Error guardando perfil: " + msg, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!petName.isEmpty()) {
                                    String petId = db.child("pets").push().getKey();
                                    if (petId != null) {
                                        Pet pet = new Pet(petId, uid, petName, "Perro", "", "");
                                        db.child("pets").child(petId).setValue(pet);
                                    }
                                }

                                progress.dismiss();
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            });
                });
    }
}