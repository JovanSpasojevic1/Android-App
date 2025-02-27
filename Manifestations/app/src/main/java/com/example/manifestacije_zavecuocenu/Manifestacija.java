package com.example.manifestacije_zavecuocenu;

import org.json.JSONObject;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Manifestacija implements Serializable {

    private int id;
    private String naziv;
    private String opis;
    private String mesto;
    private Date datumPocetka;
    private Date datumKraja;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    public String getOpis() {
        return opis;
    }
    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }
    public Date getDatumPocetka() {
        return datumPocetka;
    }

    public void setDatumPocetka(Date datumPocetka) {
        this.datumPocetka = datumPocetka;
    }

    public Date getDatumKraja() {
        return datumKraja;
    }

    public void setDatumKraja(Date datumKraja) {
        this.datumKraja = datumKraja;
    }

    public Manifestacija() {
    }

    public Manifestacija(int id, String naziv, String opis, String mesto, Date datumPocetka, Date datumKraja) {
        this.id = id;
        this.naziv = naziv;
        this.opis = opis;
        this.mesto = mesto;
        this.datumPocetka = datumPocetka;
        this.datumKraja = datumKraja;
    }

    public static Manifestacija fromJson(JSONObject o){
        Manifestacija m = new Manifestacija();
        try{
            if(o.has("id")){
                m.setId(o.getInt("id"));
            }
            if(o.has("naziv")){
                m.setNaziv(o.getString("naziv"));
            }
            if(o.has("mesto")){
                m.setMesto(o.getString("mesto"));
            }
            if(o.has("opis")){
                m.setOpis(o.getString("opis"));
            }
            if (o.has("datumPocetka")) {
                String datumPocetkaStr = o.getString("datumPocetka");
                m.setDatumPocetka(dateFormat.parse(datumPocetkaStr));
            }
            if (o.has("datumKraja")) {
                String datumKrajaStr = o.getString("datumKraja");
                m.setDatumKraja(dateFormat.parse(datumKrajaStr));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return m;
    }
}