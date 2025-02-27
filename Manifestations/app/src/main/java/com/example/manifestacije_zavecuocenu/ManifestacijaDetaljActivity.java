package com.example.manifestacije_zavecuocenu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManifestacijaDetaljActivity extends AppCompatActivity {

    private LinearLayout manifestacijaContentDetalj;
    private Button buttonBack;
    private RequestQueue requestQueue;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(this);
        manifestacijaContentDetalj = (LinearLayout) inflater.inflate(R.layout.activity_manifestacija_detalj, null);
        setContentView(manifestacijaContentDetalj);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        int manifestacijaId = intent.getIntExtra("manifestacija_id", -1);

        if (manifestacijaId != -1) {
            loadManifestacijaDetails(manifestacijaId);
        } else {
            Toast.makeText(this, "Neispravan ID manifestacije", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonBack = findViewById(R.id.buttonBack);
        if (buttonBack != null) {
            buttonBack.setOnClickListener(v -> finish());
        } else {
            Log.e("ManifestacijaDetaljActivity", "buttonBack ne radi");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadManifestacijaDetails(int manifestacijaId) {
        String url = "http://192.168.0.17:5000/json/" + manifestacijaId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Manifestacija manifestacija = Manifestacija.fromJson(response);
                    displayManifestacijaDetails(manifestacija);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Greška pri učitavanju podataka o manifestaciji", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }


    private void displayManifestacijaDetails(Manifestacija manifestacija) {
        TextView naziv, mesto, datumPocetka, datumKraja, opis;
        ImageView slika;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        naziv = manifestacijaContentDetalj.findViewById(R.id.labelNazivDetalj);
        naziv.setText(manifestacija.getNaziv());

        mesto = manifestacijaContentDetalj.findViewById(R.id.labelMestoDetalj);
        mesto.setText(manifestacija.getMesto());

        datumPocetka = manifestacijaContentDetalj.findViewById(R.id.labelDatumPocetkaDetalj);
        datumPocetka.setText(sdf.format(manifestacija.getDatumPocetka()));

        datumKraja = manifestacijaContentDetalj.findViewById(R.id.labelDatumKrajaDetalj);
        datumKraja.setText(sdf.format(manifestacija.getDatumKraja()));

        opis = manifestacijaContentDetalj.findViewById(R.id.labelOpisDetalj);
        opis.setText(manifestacija.getOpis());

        slika = manifestacijaContentDetalj.findViewById(R.id.imageView3);
        slika.setImageResource(R.drawable.livada);
    }
}
