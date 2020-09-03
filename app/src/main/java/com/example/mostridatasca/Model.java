package com.example.mostridatasca;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model {

    private static final Model ourInstance = new Model();
    private JSONArray datiMappa;
    private JSONObject datiGiocatore;
    private JSONArray datiClassifica;

    static Model getInstance(){
        return ourInstance;
    }

    private Model() {
        datiMappa = new JSONArray();
        datiGiocatore = new JSONObject();
    }

    void clear(){
        datiGiocatore = new JSONObject();
        datiMappa = new JSONArray();
    }

    //operazioni su dati mappa
    JSONObject getElementoMappa(int index) throws JSONException {
        return datiMappa.getJSONObject(index);
    }

    void setDatiMappa(JSONArray r){
        datiMappa = r;
    }

    int lengthDatiMappa(){
        return datiMappa.length();
    }


    //operazioni su dati giocatore
    JSONObject getDatiGiocatore(){
        return datiGiocatore;
    }

    void setDatiGiocatore(JSONObject g){
        datiGiocatore = g;
    }

    void setImageGiocatore(String img64){
        try {
            datiGiocatore.put("img", img64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //operazioni su dati classifica
    JSONObject getElemClassifica(int index) throws JSONException {
        return datiClassifica.getJSONObject(index);
    }

    int getSizeClassifica(){
        return datiClassifica.length();
    }

    void addClassifica(Object datiGiocatoriObject){
        datiClassifica.put(datiGiocatoriObject);
    }

    void setDatiClassifica(JSONArray classifica){
        datiClassifica = classifica;
    }

    void clearClassifica(){
        datiClassifica = new JSONArray();
    }
}
