/*
 * SCHERMATA INIZIALE DI CARICAMENTO
 */

package com.example.mostridatasca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements Style.OnStyleLoaded {

    private Model mModel = Model.getInstance();

    private TextView lifePoints;
    private TextView exp;
    private ImageButton imgUtente;

    private SharedPreferences sharedPref;

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        lifePoints = findViewById(R.id.lifePoints);
        exp = findViewById(R.id.exp);
        imgUtente = findViewById(R.id.imgUtente);
        sharedPref = getSharedPreferences("preferenze", Context.MODE_PRIVATE);

        loadingPage(true);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;

                //controllo della session_id, da qui può partire loadData() o register()
                Log.d("MyMain", "checking session_id...");
                checkFirstSession();
            }
        });

        Button btnClassifica = findViewById(R.id.btn_classifica);
        btnClassifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toClassifica = new Intent(getApplicationContext(), Classifica.class);
                startActivity(toClassifica);
            }
        });

        imgUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toProfilo = new Intent(getApplicationContext(), ProfiloUtente.class);
                startActivity(toProfilo);
            }
        });
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public boolean onAnnotationClick(Symbol symbol) {
                Log.d("MyMain", "click symbol: " + Objects.requireNonNull(symbol.getData()).toString());

                JsonObject dataFromSymbol = symbol.getData().getAsJsonObject();

                Intent toFightEat = new Intent(getApplicationContext(), FightEat.class);
                toFightEat.putExtra("data", dataFromSymbol.toString());
                startActivity(toFightEat);

                return false;
            }
        });

        style.addImage(getString(R.string.monster_icon_id), Objects.requireNonNull(getDrawable(R.drawable.monster_marker_view)));
        style.addImage(getString(R.string.candy_icon_id), Objects.requireNonNull(getDrawable(R.drawable.candy_marker_view)));

        List<SymbolOptions> monsterSymbolOptions = new ArrayList<>();
        List<SymbolOptions> candySymbolOptions = new ArrayList<>();

        //aggiunta simboli sulla mappa
        // N.B.:
        // Nel model i dati sono salvati come org.json (perche Volley usa questa libreria),
        // mentre le SymbolOptions hanno bisogno di com.google.gson. Necessario convertire i dati.
        try {
            for (int i = 0; i < mModel.lengthDatiMappa(); i++) {
                JSONObject mapObject = mModel.getElementoMappa(i);
                if (mapObject.getString("type").equals("MO")) {
                    monsterSymbolOptions.add(new SymbolOptions()
                            .withLatLng(new LatLng(mapObject.getDouble("lat"), mapObject.getDouble("lon")))
                            .withIconImage("MONSTER_ICON_ID")
                            .withData(JsonParser.parseString(mapObject.toString()).getAsJsonObject())); //da org.json a com.google.gson
                }
                else{
                    candySymbolOptions.add(new SymbolOptions()
                            .withLatLng(new LatLng(mapObject.getDouble("lat"), mapObject.getDouble("lon")))
                            .withIconImage("CANDY_ICON_ID")
                            .withData(JsonParser.parseString(mapObject.toString()).getAsJsonObject())); //da org.json a com.google.gson
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        symbolManager.create(monsterSymbolOptions);
        symbolManager.create(candySymbolOptions);

        loadingPage(false);

        //TODO aggiungere location
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(45.4, 9.19))
                .zoom(9)
                .tilt(0)
                .padding(0, 1000, 0,0 )
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 10);
    }

    private void register() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest registerRequest = new JsonObjectRequest(
                Request.Method.GET,
                getString(R.string.url_get_profile),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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
                        Log.d("MyMain", "volley error: " + error.toString());
                    }
                });
        queue.add(registerRequest);
    }

    private void loadData(){
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
                getString(R.string.url_get_profile),
                authentication,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyMain", "VolleyResponse getProfile: " + response.toString());
                        mModel.clear();
                        mModel.setDatiGiocatore(response);

                        String imgBase64 = "";

                        try {
                            lifePoints.setText("LifePoints: " + response.getString("lp"));
                            exp.setText("Experience: " + response.getString("xp"));

                            imgBase64 = response.getString("img");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //decodifica img da base64 a bitmap
                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        imgUtente.setImageBitmap(decodedByte);

                        JsonObjectRequest getMapRequest = new JsonObjectRequest(
                                getString(R.string.url_get_map),
                                authentication,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("MyMain", "VolleyResponse getMap: " + response.toString());

                                        JSONArray mapObjects;

                                        try {
                                            mapObjects = response.getJSONArray("mapobjects");
                                            mModel.setDatiMappa(mapObjects);

                                            Log.d("MyMain", "Model loaded.");

                                            //una volta scaricati gli oggetti dal server, li carico sulla mappa:
                                            mapboxMap.setStyle(Style.MAPBOX_STREETS, MainActivity.this);

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

    void checkFirstSession(){
        if (getString(R.string.test_session_id).equals("")){
            Log.d("MyMain", "test_session_id vuota, controllo shared preferences");
            if (!sharedPref.contains("session_id")){
                Log.d("MyMain", "sharedPref vuota, registering...");
                register();
            }
            else{
                Log.d("MyMain", "session id presente: " + sharedPref.getString("session_id", "NOT FOUND"));
                Log.d("MyMain", "loading data...");
                loadData();
            }
        }
        else {
            Log.d("MyMain", "test_session_id presente, aggiunta nelle sharedPref");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("session_id", getString(R.string.test_session_id));
            editor.apply();

            Log.d("MyMain", "loading data...");
            loadData();
        }
    }

    void loadingPage(boolean start){
        //se start == true -> loadingPage attivo
        if (start){
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            FragmentCaricamento schermataCaricamento = new FragmentCaricamento();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, schermataCaricamento);
            transaction.commit();
        }
        else{
            findViewById(R.id.fragment_container).setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}