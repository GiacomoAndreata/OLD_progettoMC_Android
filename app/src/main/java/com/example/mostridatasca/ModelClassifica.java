package com.example.mostridatasca;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelClassifica {

    public static final ModelClassifica instance = new ModelClassifica();
    private JSONArray datiClassifica;

    private ModelClassifica(){
        datiClassifica = new JSONArray();
    }

    public static ModelClassifica getInstance(){
        return instance;
    };

    JSONObject get(int index) throws JSONException {
        return datiClassifica.getJSONObject(index);
    }

    int getSize(){
        return datiClassifica.length();
    }

    void addClassifica(Object datiGiocatoriObject){
        Log.d("ModelADD", "Add: " + datiGiocatoriObject.toString());
        datiClassifica.put(datiGiocatoriObject);
    }

    void clearClassifica(){
        datiClassifica = new JSONArray();
    }
}
