package com.example.manifestacije_zavecuocenu;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class HomeFragment extends Fragment {

    private ManifestacijaFragmentHelper helper;
    private LinearLayout mainLayout;
    private ArrayList<Manifestacija> manifestacije;
    private View rootView;
    private RequestQueue requestQueue;
    private ActivityResultLauncher<Intent> addManifestacijaLauncher;
    private ActivityResultLauncher<Intent> editManifestacijaLauncher;
    private EditText search;
    private ArrayList<Manifestacija> shownManifestacije;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainLayout = rootView.findViewById(R.id.mainLayout);
        manifestacije = new ArrayList<>();
        helper = new ManifestacijaFragmentHelper(getContext());
        requestQueue = Volley.newRequestQueue(getContext());

        addManifestacijaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        initManifestacije();
                    }
                });

        editManifestacijaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        initManifestacije();
                    }
                });

        setupAddButton();
        setupSearch();
        initManifestacije();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initManifestacije();
    }

    private void initManifestacije() {
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

                            manifestacije.add(manifestacija);
                        }

                        shownManifestacije = new ArrayList<>(manifestacije);
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

    private void setupSearch() {
        search = rootView.findViewById(R.id.inputSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shownManifestacije = new ArrayList<>();
                String searchText = search.getText().toString().toLowerCase(Locale.ROOT);
                for (Manifestacija m : manifestacije) {
                    if (m.getNaziv().toLowerCase(Locale.ROOT).contains(searchText) ||
                            m.getMesto().toLowerCase(Locale.ROOT).contains(searchText)) {
                        shownManifestacije.add(m);
                    }
                }
                displayManifestacije();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void setupAddButton() {
        ImageView addButton = rootView.findViewById(R.id.imgDODAJ);
        addButton.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), EditManifestacijaActivity.class);
            addManifestacijaLauncher.launch(i);
        });
    }

    private void displayManifestacije() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mainLayout.removeAllViews();

        for (Manifestacija manifestacija : shownManifestacije) {
            View manifestacijaView = helper.createManifestacijaView(inflater, mainLayout, manifestacija, editManifestacijaLauncher, this, false);
            mainLayout.addView(manifestacijaView);
        }
    }
}