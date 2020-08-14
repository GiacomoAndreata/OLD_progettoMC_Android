/*
 * Tabella classifica primi 20 giocatori
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Classifica extends AppCompatActivity {

    private final static String URL_GET_RANKING = "https://ewserver.di.unimi.it/mobicomp/mostri/ranking.php";
    private final static JSONObject AUTHENTICATION = new JSONObject();
    //private final String SESSION_ID = getString(R.string.test_session_id);
    private final static String SESSION_ID = "8eOWKEcSInwRNBwC";
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);

        try {
            AUTHENTICATION.put("session_id", SESSION_ID);
        }catch (JSONException e){
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        loadData();
    }

    //TODO caricamento: dati giocatori
    public void loadData(){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                URL_GET_RANKING,
                AUTHENTICATION,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley", "Corrent: " + response.toString());

                        //SALVATAGGIO DEI DATI
                        JSONArray datiGiocatori = null;
                        ModelClassifica modelClassifica = ModelClassifica.getInstance();
                        modelClassifica.clear();

                        try {
                            datiGiocatori = response.getJSONArray("ranking");
                            for (int i = 0; i < datiGiocatori.length(); i++){
                                JSONObject o = datiGiocatori.getJSONObject(i);
                                modelClassifica.add(o);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                , new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "VolleyError getMap: " + error.toString());
            }
        });
        Log.d("Volledy", "sending request");
        mRequestQueue.add(request);
    }
}