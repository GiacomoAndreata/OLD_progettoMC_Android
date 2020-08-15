/*
 * SCHERMATA INIZIALE DI CARICAMENTO
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    final static String URL_REGISTER = "https://ewserver.di.unimi.it/mobicomp/mostri/register.php";
    final static String URL_GET_PROFILE = "https://ewserver.di.unimi.it/mobicomp/mostri/getprofile.php";
    final static String URL_GET_MAP = "https://ewserver.di.unimi.it/mobicomp/mostri/getmap.php";
    private Model mModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        mModel = Model.getInstance();

        if (getString(R.string.test_session_id).equals("")){
            Log.d("MyMain", "test_session_id uguale a stringa vuota");
            if (!sharedPref.contains("session_id")){
                Log.d("MyMain", "registering...");
                register();
            }
            else{
                Log.d("MyMain", "session id salvata: " + sharedPref.getString("session_id", "NOT FOUND"));

                loadData();
            }
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("session_id", getString(R.string.test_session_id));
            editor.apply();
            Log.d("MyMain", "session id from test_session_id: " + sharedPref.getString("session_id", "NOT FOUND"));

            loadData();
        }
    }

    private void register() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_REGISTER,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("session_id", response.getString("session_id"));
                            editor.apply();
                            Log.d("MyMain", "session id from register: " + sharedPref.getString("session_id", "NOT FOUND"));

                            loadData();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO gestire errore di connessione
                        Log.d("MyMain", "volley error");
                    }
                });
        queue.add(jsonObjectRequest);
    }

    //TODO download dati da server e caricamento mappa

    private void loadData(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String session_id = sharedPref.getString("session_id", "");

        final JSONObject authentication = new JSONObject();
        try {
            authentication.put("session_id", session_id);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        final RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getProfileRequest = new JsonObjectRequest(
                URL_GET_PROFILE,
                authentication,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyMain", "VolleyResponse getProfile: " + response.toString());
                        mModel.clear();
                        mModel.addDatiGiocatore(response);

                        JsonObjectRequest getMapRequest = new JsonObjectRequest(
                                URL_GET_MAP,
                                authentication,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("MyMain", "VolleyResponse getMap: " + response.toString());

                                        JSONArray mapObjects = null;

                                        try {
                                            mapObjects = response.getJSONArray("mapobjects");
                                            mModel.addDatiMappa(mapObjects);

                                            Log.d("MyMain", "Model loaded.");
                                            Log.d("MyMain", "dati giocatore: " + mModel.getDatiGiocatore().toString());
                                            Log.d("MyMain", "dati mappa: " + mModel.getDatiMappa().toString());


                                            Intent intent = new Intent(getApplicationContext(), Mappa.class);
                                            startActivity(intent);

                                        }
                                        catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("MyMain", "VolleyError getMap: " + error.toString());
                            }
                        });

                        queue.add(getMapRequest);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyMain", "VolleyError getProfile: " + error.toString());
                    }
                });
        queue.add(getProfileRequest);
    }

    //TODO metodo animazione barra di caricamento --> fine rimando a schermata mappa
}