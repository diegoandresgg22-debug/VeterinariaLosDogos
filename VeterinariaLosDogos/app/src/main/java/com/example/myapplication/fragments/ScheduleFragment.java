package com.example.myapplication.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFragment extends Fragment {

    private EditText etDate, etTime, etReason;
    private Button btnSave;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private final Calendar calendar = Calendar.getInstance();

    public ScheduleFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule, container, false);

        etDate = v.findViewById(R.id.etDate);
        etTime = v.findViewById(R.id.etTime);
        etReason = v.findViewById(R.id.etReason);
        btnSave = v.findViewById(R.id.btnSave);

        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        etDate.setOnClickListener(v1 -> showDatePicker());
        etTime.setOnClickListener(v2 -> showTimePicker());

        btnSave.setOnClickListener(v3 -> saveAppointment());
        return v;
    }

    private void showDatePicker() {
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etDate.setText(df.format(calendar.getTime()));
        }, y, m, d).show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
        }, hour, min, true).show();
    }

    private void saveAppointment() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(reason)) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = db.child("appointments").push().getKey();
        db.child("appointments").child(id).child("userId").setValue(uid);
        db.child("appointments").child(id).child("date").setValue(date);
        db.child("appointments").child(id).child("time").setValue(time);
        db.child("appointments").child(id).child("reason").setValue(reason);

        Toast.makeText(getContext(), "Hora agendada", Toast.LENGTH_SHORT).show();
        etDate.setText("");
        etTime.setText("");
        etReason.setText("");
    }
}

