package com.example.manifestacije_zavecuocenu;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditManifestacijaActivity extends AppCompatActivity {

    private Manifestacija m;
    private EditText naziv, mesto, datumPocetka, datumKraja, opis;
    private Button confirm, cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_manifestacija);

        EditText editDatumPocetka = findViewById(R.id.editDatumPocetka);
        EditText editDatumKraja = findViewById(R.id.editDatumKraja);

        naziv = findViewById(R.id.editNaziv);
        mesto = findViewById(R.id.editMesto);
        datumPocetka = findViewById(R.id.editDatumPocetka);
        datumKraja = findViewById(R.id.editDatumKraja);
        opis = findViewById(R.id.editOpis);

        confirm = findViewById(R.id.confirmButton);
        cancel = findViewById(R.id.forfeitButton);

        editDatumPocetka.setOnClickListener(view -> showDatePickerDialog(editDatumPocetka));
        editDatumKraja.setOnClickListener(view -> showDatePickerDialog(editDatumKraja));

        m = (Manifestacija) getIntent().getSerializableExtra("manifestacija");
        if (m == null) {
            addManifestacija();
        } else {
            editManifestacija();
        }
    }

    public void addManifestacija() {

        m = new Manifestacija();

        cancel.setOnClickListener((view) -> {
            finish();
        });

        confirm.setOnClickListener((view) -> {
            m.setNaziv(naziv.getText().toString());
            m.setMesto(mesto.getText().toString());
            m.setOpis(opis.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            try {
                Date parsedDatumPocetka = sdf.parse(datumPocetka.getText().toString());
                Date parsedDatumKraja = sdf.parse(datumKraja.getText().toString());
                m.setDatumPocetka(parsedDatumPocetka);
                m.setDatumKraja(parsedDatumKraja);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Greška pri unosu datuma", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("naziv", m.getNaziv());
                jsonObject.put("mesto", m.getMesto());
                jsonObject.put("datumPocetka", sdf.format(m.getDatumPocetka()));
                jsonObject.put("datumKraja", sdf.format(m.getDatumKraja()));
                jsonObject.put("opis", m.getOpis());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.0.17:5000/json";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        try {
                            // Parse the 'id' from the response
                            int newId = response.getInt("id");
                            m.setId(newId);

                            Toast.makeText(EditManifestacijaActivity.this, "Manifestacija je kreirana", Toast.LENGTH_SHORT).show();

                            setResult(RESULT_OK); // Indicate success
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditManifestacijaActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(EditManifestacijaActivity.this, "Error adding manifestacija", Toast.LENGTH_SHORT).show();
                    });

            queue.add(request);
        });
    }

    public void editManifestacija(){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        naziv.setText(m.getNaziv());
        mesto.setText(m.getMesto());
        if (m.getDatumPocetka() != null) {
            datumPocetka.setText(sdf.format(m.getDatumPocetka()));
        }
        if (m.getDatumKraja() != null) {
            datumKraja.setText(sdf.format(m.getDatumKraja()));
        }
        opis.setText(m.getOpis());

        cancel.setOnClickListener((view) -> {
            finish();
        });

        confirm.setOnClickListener((view) -> {
            m.setNaziv(naziv.getText().toString());
            m.setMesto(mesto.getText().toString());
            m.setOpis(opis.getText().toString());

            try {
                Date parsedDatumPocetka = sdf.parse(datumPocetka.getText().toString());
                Date parsedDatumKraja = sdf.parse(datumKraja.getText().toString());
                m.setDatumPocetka(parsedDatumPocetka);
                m.setDatumKraja(parsedDatumKraja);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Greška pri unosu datuma", Toast.LENGTH_SHORT).show();
                return; // Exit if parsing fails
            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", m.getId());
                jsonObject.put("naziv", m.getNaziv());
                jsonObject.put("mesto", m.getMesto());
                jsonObject.put("datumPocetka", sdf.format(m.getDatumPocetka()));
                jsonObject.put("datumKraja", sdf.format(m.getDatumKraja()));
                jsonObject.put("opis", m.getOpis());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://192.168.0.17:5000/json/" + m.getId();

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                    response -> {
                        Toast.makeText(EditManifestacijaActivity.this, "Manifestacija je uspešno ažurirana", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Dodato
                        finish(); // Završavanje aktivnosti
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(EditManifestacijaActivity.this, "Error updating manifestacija", Toast.LENGTH_SHORT).show();
                    });
            queue.add(request);
        });
    }

    private void showDatePickerDialog(final EditText dateField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    dateField.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}
