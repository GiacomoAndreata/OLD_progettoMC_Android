package com.example.mostridatasca;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelClassifica {

    public static final ModelClassifica instance = new ModelClassifica();
    private JSONArray datiClssifica;

    private ModelClassifica(){
        datiClssifica = new JSONArray();
    }

    public static ModelClassifica getInstance(){
        return instance;
    };

    JSONObject get(int index) throws JSONException {
        return datiClssifica.getJSONObject(index);
    }

    int getSize(){
        return datiClssifica.length();
    }

    void addClassifica(Object datiGiocatoriObject){
        Log.d("ModelADD", "Add: " + datiGiocatoriObject.toString());
        datiClssifica.put(datiGiocatoriObject);
    }

    void clearClassifica(){
        datiClssifica = new JSONArray();
    }
}
