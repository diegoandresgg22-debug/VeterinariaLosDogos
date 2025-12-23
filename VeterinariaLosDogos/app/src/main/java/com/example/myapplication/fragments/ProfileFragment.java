package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.models.IoTStatus;
import com.example.myapplication.models.Pet;
import com.example.myapplication.models.User;
import com.example.myapplication.repository.IoTRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private LinearLayout petsContainer;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

    private Query petsQuery;
    private ValueEventListener petsListener;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tvUserName = v.findViewById(R.id.tvUserName);
        tvUserEmail = v.findViewById(R.id.tvUserEmail);
        petsContainer = v.findViewById(R.id.petsContainer);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        loadUser();
    }

    private void loadUser() {
        FirebaseUser current = mAuth.getCurrentUser();
        if (current == null) return;

        db.child("users").child(current.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User u = snapshot.getValue(User.class);
                        if (u != null) {
                            tvUserName.setText(u.getName());
                            tvUserEmail.setText(u.getEmail());
                            loadPets(current.getUid());
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void loadPets(@NonNull String ownerId) {
        petsContainer.removeAllViews();

        petsQuery = db.child("pets")
                .orderByChild("ownerId")
                .equalTo(ownerId);

        petsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot sn) {
                petsContainer.removeAllViews();
                List<Pet> pets = new ArrayList<>();

                for (DataSnapshot child : sn.getChildren()) {
                    Pet p = child.getValue(Pet.class);
                    if (p != null) pets.add(p);
                }

                for (Pet p : pets) {
                    addPetView(p);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        petsQuery.addValueEventListener(petsListener);
    }

    private void addPetView(@NonNull Pet p) {
        int pad = (int) (8 * getResources().getDisplayMetrics().density);

        // Nombre de la mascota
        TextView tv = new TextView(getContext());
        tv.setPadding(pad, pad, pad, pad);
        tv.setText("🐾 " + p.getName() + " (" + p.getSpecies() + ")");
        petsContainer.addView(tv);

        // TextView para mostrar estado IoT
        TextView tvIot = new TextView(getContext());
        tvIot.setPadding(pad, 0, pad, pad);
        tvIot.setText("Cargando datos IoT…");
        petsContainer.addView(tvIot);

        // Repositorio IoT
        IoTRepository ioTRepo = new IoTRepository();

        // Escuchar estado IoT SOLO si la mascota tiene id
        if (p.getId() != null) {
            ioTRepo.listenToStatus(p.getId(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    IoTStatus st = snapshot.getValue(IoTStatus.class);
                    if (st == null) {
                        tvIot.setText("Sin datos IoT");
                        return;
                    }

                    tvIot.setText(
                            "Temp: " + st.getTemperature() + "°C\n" +
                                    "FC: " + st.getHeartRate() + " lpm\n" +
                                    "Actividad: " + st.getActivityLevel() + "\n" +
                                    "Puerta: " + (st.isDoorOpen() ? "Abierta" : "Cerrada") + "\n" +
                                    "Buzzer: " + (st.isBuzzerActive() ? "Activo" : "Off") + "\n" +
                                    "Estado: " + st.getState()
                    );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    tvIot.setText("Error al leer IoT: " + error.getMessage());
                }
            });
        } else {
            tvIot.setText("Mascota sin ID, no se puede vincular IoT.");
        }

        // Botón para enviar comando a actuador (buzzer/LED en el microcontrolador)
        Button btn = new Button(getContext());
        btn.setText("Activar alarma IoT");
        btn.setOnClickListener(v -> {
            if (p.getId() != null) {
                sendIotCommand(p.getId(), "ALARM_ON");
            } else {
                Toast.makeText(getContext(), "Mascota sin ID para IoT", Toast.LENGTH_SHORT).show();
            }
        });

        petsContainer.addView(btn);
    }

    // Mantengo este método para enviar comandos IoT (actuadores)
    private void sendIotCommand(@NonNull String petId, @NonNull String command) {
        String cmdId = db.child("iotCommands").child(petId).push().getKey();
        if (cmdId == null) return;

        Map<String, Object> payload = new HashMap<>();
        payload.put("command", command);
        payload.put("timestamp", ServerValue.TIMESTAMP);

        db.child("iotCommands").child(petId).child(cmdId)
                .setValue(payload)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Comando enviado", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (petsQuery != null && petsListener != null)
            petsQuery.removeEventListener(petsListener);
    }
}

