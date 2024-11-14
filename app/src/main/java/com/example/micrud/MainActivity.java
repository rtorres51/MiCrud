package com.example.micrud;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText etClientName, etDate, etTime;
    Spinner spinnerService;
    Button btnAddAppointment, btnUpdateAppointment;
    ListView listViewAppointments;

    ArrayList<String> appointmentList;
    ArrayAdapter<String> adapter;
    int selectedAppointmentId = -1;  // ID de la cita seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        etClientName = findViewById(R.id.etClientName);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        spinnerService = findViewById(R.id.spinnerService);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        btnUpdateAppointment = findViewById(R.id.btnUpdateAppointment);
        listViewAppointments = findViewById(R.id.listViewAppointments);

        // Configurar el Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.services_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(spinnerAdapter);

        // Configurar los selectores de fecha y hora
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        // Mostrar las citas existentes
        loadAppointments();

        // Agregar una nueva cita
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointment();
            }
        });

        // Actualizar una cita seleccionada
        btnUpdateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAppointment();
            }
        });

        // Seleccionar una cita desde el ListView
        listViewAppointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectAppointment(position);
            }
        });

        // Eliminar una cita haciendo una pulsación larga
        listViewAppointments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteAppointment(position);
                return true;
            }
        });
    }

    // Cargar citas desde la base de datos y actualizar el ListView
    private void loadAppointments() {
        appointmentList = new ArrayList<>();
        Cursor cursor = db.getAllAppointments();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No hay citas registradas", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                // Formato: ID - Cliente - Servicio - Fecha - Hora
                appointmentList.add(cursor.getString(0) + " - " + cursor.getString(1) + " - " + cursor.getString(2) + " - " + cursor.getString(3) + " - " + cursor.getString(4));
            }
        }

        // Vincular el ListView con el adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appointmentList);
        listViewAppointments.setAdapter(adapter);
    }

    // Mostrar DatePickerDialog para seleccionar la fecha
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            etDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    // Mostrar TimePickerDialog para seleccionar la hora
    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            etTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    // Agregar una nueva cita
    private void addAppointment() {
        String clientName = etClientName.getText().toString();
        String service = spinnerService.getSelectedItem().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();

        if (clientName.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = db.insertAppointment(clientName, service, date, time);
        if (isInserted) {
            Toast.makeText(this, "Cita agregada exitosamente", Toast.LENGTH_SHORT).show();
            clearFields();
            loadAppointments(); // Recargar el ListView después de agregar
        } else {
            Toast.makeText(this, "Error al agregar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    // Actualizar una cita existente
    private void updateAppointment() {
        if (selectedAppointmentId == -1) {
            Toast.makeText(this, "Seleccione una cita para actualizar", Toast.LENGTH_SHORT).show();
            return;
        }

        String clientName = etClientName.getText().toString();
        String service = spinnerService.getSelectedItem().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();

        if (clientName.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = db.updateAppointment(selectedAppointmentId, clientName, service, date, time);
        if (isUpdated) {
            Toast.makeText(this, "Cita actualizada exitosamente", Toast.LENGTH_SHORT).show();
            clearFields();
            loadAppointments(); // Recargar el ListView después de actualizar
            btnUpdateAppointment.setVisibility(View.GONE);
            btnAddAppointment.setVisibility(View.VISIBLE);
            selectedAppointmentId = -1;
        } else {
            Toast.makeText(this, "Error al actualizar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    // Seleccionar una cita y mostrar los detalles en los campos de texto
    private void selectAppointment(int position) {
        String selected = appointmentList.get(position);
        String[] parts = selected.split(" - ");

        selectedAppointmentId = Integer.parseInt(parts[0]); // Extraer el ID de la cita

        etClientName.setText(parts[1]); // Nombre del cliente
        etDate.setText(parts[3]);       // Fecha
        etTime.setText(parts[4]);       // Hora

        // Seleccionar el servicio en el Spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerService.getAdapter();
        int spinnerPosition = adapter.getPosition(parts[2]);
        spinnerService.setSelection(spinnerPosition);

        // Mostrar el botón de actualizar y ocultar el de agregar
        btnAddAppointment.setVisibility(View.GONE);
        btnUpdateAppointment.setVisibility(View.VISIBLE);
    }

    // Eliminar una cita
    private void deleteAppointment(int position) {
        String selected = appointmentList.get(position);
        String[] parts = selected.split(" - ");
        int appointmentId = Integer.parseInt(parts[0]); // Extraer el ID de la cita

        boolean isDeleted = db.deleteAppointment(appointmentId);
        if (isDeleted) {
            Toast.makeText(this, "Cita eliminada exitosamente", Toast.LENGTH_SHORT).show();
            loadAppointments(); // Recargar el ListView después de eliminar
        } else {
            Toast.makeText(this, "Error al eliminar la cita", Toast.LENGTH_SHORT).show();
        }
    }

    // Limpiar los campos de texto
    private void clearFields() {
        etClientName.setText("");
        etDate.setText("");
        etTime.setText("");
        spinnerService.setSelection(0);
    }
}