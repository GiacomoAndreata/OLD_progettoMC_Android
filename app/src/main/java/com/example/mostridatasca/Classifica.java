/*
 * Tabella classifica primi 20 giocatori
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Classifica extends AppCompatActivity {

    private final static JSONObject AUTHENTICATION = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);

        SharedPreferences sharedPref = getSharedPreferences("preferenze", Context.MODE_PRIVATE);
        String SESSION_ID = sharedPref.getString("session_id", "");

        try {
            AUTHENTICATION.put("session_id", SESSION_ID);
        }catch (JSONException e){
            e.printStackTrace();
        }

        loadData();
    }

    void setRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    //TODO migliorare grafica


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void loadData(){
        Log.d("classifica", "loading data");
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest getRankingRequest = new JsonObjectRequest(
                getString(R.string.url_get_ranking),
                AUTHENTICATION,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("classifica", "getRanking response: " + response.toString());

                        //SALVATAGGIO DEI DATI
                        Model mModel = Model.getInstance();
                        mModel.clearClassifica();

                        try {
                            mModel.setDatiClassifica(response.getJSONArray("ranking"));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        setRecyclerView();
                    }
                }
                , new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("classifica", "getRanking VolleyError: " + error.toString());
            }
        });
        Log.d("classifica", "getRankingRequest aggiunta nella queue");
        mRequestQueue.add(getRankingRequest);
    }
}