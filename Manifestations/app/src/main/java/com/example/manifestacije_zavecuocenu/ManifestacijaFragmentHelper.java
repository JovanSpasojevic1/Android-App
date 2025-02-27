package com.example.manifestacije_zavecuocenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ManifestacijaFragmentHelper {

    private Context context;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    public ManifestacijaFragmentHelper(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        this.requestQueue = Volley.newRequestQueue(context);
    }


    public void toggleOmiljene(int id) {
        Set<String> omiljene = new HashSet<>(sharedPreferences.getStringSet("favorite_ids", new HashSet<>())) ;

        if (omiljene.contains(String.valueOf(id))) {
            omiljene.remove(String.valueOf(id));
        } else {
            omiljene.add(String.valueOf(id));
        }
        sharedPreferences.edit().putStringSet("favorite_ids", omiljene).apply();
    }

    // Provera da li je manifestacija omiljena
    public boolean proveraOmiljene(int id) {
        Set<String> favoriteIds = sharedPreferences.getStringSet("favorite_ids", new HashSet<>());
        return favoriteIds.contains(String.valueOf(id));
    }

    // Brisanje manifestacije
    public void deleteManifestacija(int id, Runnable onSuccess, Runnable onError) {
        String url = "http://192.168.0.17:5000/json/" + id;
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> onSuccess.run(),
                error -> {
                    error.printStackTrace();
                    onError.run();
                });
        requestQueue.add(request);
    }

    public View createManifestacijaView(LayoutInflater inflater, ViewGroup container, Manifestacija manifestacija, ActivityResultLauncher<Intent> editLauncher, Fragment fragment, boolean isEditView) {
        View subLayout = inflater.inflate(R.layout.manifestacija_layout, container, false);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        TextView naziv = subLayout.findViewById(R.id.labelNaziv);
        naziv.setText(manifestacija.getNaziv());

        TextView mesto = subLayout.findViewById(R.id.labelMesto);
        mesto.setText(manifestacija.getMesto());

        TextView datumPocetka = subLayout.findViewById(R.id.labelDatumPocetka);
        datumPocetka.setText(sdf.format(manifestacija.getDatumPocetka()));

        TextView datumKraja = subLayout.findViewById(R.id.labelDatumKraja);
        datumKraja.setText(sdf.format(manifestacija.getDatumKraja()));

        ImageView slika = subLayout.findViewById(R.id.labelSlikaSVE);
        slika.setImageResource(R.drawable.livada);

        // Brisanje manifestacije
        ImageView delButton = subLayout.findViewById(R.id.labelDelete);
        delButton.setOnClickListener(view -> deleteManifestacija(manifestacija.getId(),
                () -> {
                    Toast.makeText(context, "Manifestacija obrisana", Toast.LENGTH_SHORT).show();
                    fragment.onResume();
                },
                () -> Toast.makeText(context, "Greška pri brisanju manifestacije", Toast.LENGTH_SHORT).show()));

        // Editovanje manifestacije
        ImageView editButton = subLayout.findViewById(R.id.labelEdit);
        if (isEditView) {
            // Sakrij dugme za edit i delete
            editButton.setVisibility(View.GONE);
            delButton.setVisibility(View.GONE);
        } else {
            editButton.setOnClickListener(view -> {
                Intent i = new Intent(context, EditManifestacijaActivity.class);
                i.putExtra("manifestacija", manifestacija);
                editLauncher.launch(i);
            });
        }

        // Prikaz detalja manifestacije
        LinearLayout manifestacijeContent = subLayout.findViewById(R.id.manifestacijaContent);
        manifestacijeContent.setOnClickListener(view -> {
            Intent i = new Intent(context, ManifestacijaDetaljActivity.class);
            i.putExtra("manifestacija_id", manifestacija.getId());
            context.startActivity(i);
        });

        // Favorizovanje manifestacije
        ImageView favoriteButton = subLayout.findViewById(R.id.labelFavorite);
        updateOmiljIcon(favoriteButton, manifestacija.getId());
        favoriteButton.setOnClickListener(view -> {
            toggleOmiljene(manifestacija.getId());
            updateOmiljIcon(favoriteButton, manifestacija.getId());
        });

        return subLayout;
    }

    private void updateOmiljIcon(ImageView favoriteButton, int id) {
        if (proveraOmiljene(id)) {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_on); // Popunjena zvezdica
        } else {
            favoriteButton.setImageResource(android.R.drawable.btn_star_big_off); // Prazna zvezdica
        }
    }

}

