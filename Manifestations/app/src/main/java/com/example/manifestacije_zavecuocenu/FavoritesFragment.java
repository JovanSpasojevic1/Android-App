package com.example.manifestacije_zavecuocenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private ManifestacijaFragmentHelper helper;
    private LinearLayout mainLayout;
    private ArrayList<Manifestacija> manifestacije;
    private View rootView;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        mainLayout = rootView.findViewById(R.id.mainLayout);
        manifestacije = new ArrayList<>();
        helper = new ManifestacijaFragmentHelper(getContext());
        requestQueue = Volley.newRequestQueue(getContext());

        // Učitaj omiljene manifestacije
        initManifestacije();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initManifestacije();
    }

    private void initManifestacije() {
        Set<Integer> favoriteIds = getFavoriteIds();

        String url = "http://192.168.0.17:5000/json";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    manifestacije.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Manifestacija manifestacija = new Manifestacija();

                            manifestacija.setId(jsonObject.getInt("id"));
                            manifestacija.setNaziv(jsonObject.getString("naziv"));
                            manifestacija.setMesto(jsonObject.getString("mesto"));
                            manifestacija.setOpis(jsonObject.getString("opis"));

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                            manifestacija.setDatumPocetka(sdf.parse(jsonObject.getString("datumPocetka")));
                            manifestacija.setDatumKraja(sdf.parse(jsonObject.getString("datumKraja")));

                            if (favoriteIds.contains(manifestacija.getId())) {
                                manifestacije.add(manifestacija);
                            }
                        }

                        displayManifestacije();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private Set<Integer> getFavoriteIds() {
        SharedPreferences prefs = getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        Set<String> favoriteIdsString = prefs.getStringSet("favorite_ids", new HashSet<>());
        Set<Integer> favoriteIds = new HashSet<>();
        if (favoriteIdsString != null) {
            for (String idStr : favoriteIdsString) {
                favoriteIds.add(Integer.parseInt(idStr));
            }
        }
        return favoriteIds;
    }

    private void displayManifestacije() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mainLayout.removeAllViews();

        for (Manifestacija manifestacija : manifestacije) {
            View manifestacijaView = helper.createManifestacijaView(inflater, mainLayout, manifestacija, null, this, true); // Prošlo true za favorite
            mainLayout.addView(manifestacijaView);
        }
    }

}