package com.example.mostridatasca;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Model {

    private static final Model ourInstance = new Model();
    private JSONArray datiMappa;
    private JSONObject datiGiocatore;

    static Model getInstance(){
        return ourInstance;
    }

    private Model() {
        datiMappa = new JSONArray();
        datiGiocatore = new JSONObject();
    }

    JSONObject getElementoMappa(int index) throws JSONException {
        return datiMappa.getJSONObject(index);
    }

    public JSONArray getDatiMappa() {
        return datiMappa;
    }

    JSONObject getDatiGiocatore(){
        return datiGiocatore;
    }

    int getSizeMappa(){
        return datiMappa.length();
    }

    void addElementoMappa(Object mapObject){
        datiMappa.put(mapObject);
    }

    void addDatiMappa(JSONArray r){
        datiMappa = r;
    }

    void addDatiGiocatore(JSONObject g){
        datiGiocatore = g;
    }

    void setImageUtente(String img64){
        try {
            datiGiocatore.put("img", img64);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setUsername(String username){
        try {
            datiGiocatore.put("username", username);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    void clearMappa(){
        datiMappa = new JSONArray();
    }

    void clearGiocatore(){
        datiGiocatore = new JSONObject();
    }

    void clear(){
        datiGiocatore = new JSONObject();
        datiMappa = new JSONArray();
    }
}
