package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myapplication.models.Pet;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterPetActivity extends AppCompatActivity {

    private EditText etPetName, etBreed, etBirthdate;
    private Spinner spSpecies;
    private Button btnSavePet;
    private ProgressDialog progress;
    private DatabaseReference db;
    private FirebaseAuth mAuth;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pet); // crea este layout

        etPetName = findViewById(R.id.etPetName);
        etBreed = findViewById(R.id.etBreed);
        etBirthdate = findViewById(R.id.etBirthdate);
        spSpecies = findViewById(R.id.spSpecies);
        btnSavePet = findViewById(R.id.btnSavePet);

        // Spinner species
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.species_array, android.R.layout.simple_spinner_item); // define array en strings.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpecies.setAdapter(adapter);

        progress = new ProgressDialog(this);
        progress.setMessage("Guardando mascota...");

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        etBirthdate.setOnClickListener(v -> showDatePicker());

        btnSavePet.setOnClickListener(v -> savePet());
    }

    private void showDatePicker() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etBirthdate.setText(isoFormat.format(calendar.getTime()));
        }, y, m, d);
        dp.show();
    }

    private void savePet() {
        String petName = etPetName.getText().toString().trim();
        String breed = etBreed.getText().toString().trim();
        String species = spSpecies.getSelectedItem() != null ? spSpecies.getSelectedItem().toString() : "Otro";
        String birthdate = etBirthdate.getText().toString().trim();

        if (TextUtils.isEmpty(petName)) {
            etPetName.setError("Nombre de la mascota requerido");
            etPetName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(birthdate)) {
            etBirthdate.setError("Fecha de nacimiento requerida");
            etBirthdate.requestFocus();
            return;
        }

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.show();
        String petId = db.child("pets").push().getKey();
        Pet pet = new Pet(petId, uid, petName, species, breed, birthdate);
        db.child("pets").child(petId).setValue(pet)
                .addOnCompleteListener(task -> {
                    progress.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterPetActivity.this, "Mascota guardada", Toast.LENGTH_SHORT).show();
                        finish(); // vuelve al perfil/main
                    } else {
                        Toast.makeText(RegisterPetActivity.this, "Error guardando mascota: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
