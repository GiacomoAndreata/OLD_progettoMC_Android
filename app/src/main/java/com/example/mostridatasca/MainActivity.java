/*
 * SCHERMATA INIZIALE DI CARICAMENTO
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    final static String URL_REGISTER = "https://ewserver.di.unimi.it/mobicomp/mostri/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TEST PER CLASSIFICA:

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(getApplicationContext(), Classifica.class);

         */

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);


        if (getString(R.string.test_session_id).equals("")){
            Log.d("MyMain", "test_session_id uguale a stringa vuota");
            if (!sharedPref.contains("session_id")){
                Log.d("MyMain", "registering...");
                register();
            }
            else{
                Log.d("MyMain", "session id salvata: " + sharedPref.getString("session_id", "NOT FOUND"));
            }
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("session_id", getString(R.string.test_session_id));
            editor.apply();
            Log.d("MyMain", "session id from test_session_id: " + sharedPref.getString("session_id", "NOT FOUND"));
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

    //TODO metodo animazione barra di caricamento --> fine rimando a schermata mappa
}