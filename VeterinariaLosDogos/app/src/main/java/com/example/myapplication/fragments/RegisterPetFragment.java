package com.example.myapplication.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.myapplication.R;
import com.example.myapplication.models.Pet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterPetFragment extends Fragment {

    private EditText etPetName, etBreed, etBirthdate;
    private Spinner spSpecies;
    private Button btnSavePet;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public RegisterPetFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register_pet, container, false);

        etPetName = v.findViewById(R.id.etPetName);
        etBreed = v.findViewById(R.id.etBreed);
        etBirthdate = v.findViewById(R.id.etBirthdate);
        spSpecies = v.findViewById(R.id.spSpecies);
        btnSavePet = v.findViewById(R.id.btnSavePet);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.species_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSpecies.setAdapter(adapter);

        etBirthdate.setOnClickListener(v1 -> showDatePicker());
        btnSavePet.setOnClickListener(v12 -> savePet());

        return v;
    }

    private void showDatePicker() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            etBirthdate.setText(isoFormat.format(calendar.getTime()));
        }, y, m, d).show();
    }

    private void savePet() {
        String petName = etPetName.getText().toString().trim();
        String breed = etBreed.getText().toString().trim();
        String species = spSpecies.getSelectedItem().toString();
        String birthdate = etBirthdate.getText().toString().trim();

        if (TextUtils.isEmpty(petName)) {
            etPetName.setError("Nombre requerido");
            return;
        }
        if (TextUtils.isEmpty(birthdate)) {
            etBirthdate.setError("Fecha requerida");
            return;
        }

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String petId = db.child("pets").push().getKey();
        Pet pet = new Pet(petId, uid, petName, species, breed, birthdate);
        db.child("pets").child(petId).setValue(pet)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Mascota registrada", Toast.LENGTH_SHORT).show();
                        etPetName.setText("");
                        etBreed.setText("");
                        etBirthdate.setText("");
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}

