/*
 * SCHERMATA INIZIALE DI CARICAMENTO
 */

package com.example.mostridatasca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
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
import com.google.gson.JsonParser;
import com.mapbox.mapboxsdk.Mapbox;
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

    final static String URL_REGISTER = "https://ewserver.di.unimi.it/mobicomp/mostri/register.php";
    final static String URL_GET_PROFILE = "https://ewserver.di.unimi.it/mobicomp/mostri/getprofile.php";
    final static String URL_GET_MAP = "https://ewserver.di.unimi.it/mobicomp/mostri/getmap.php";

    private Model mModel = Model.getInstance();

    private MapView mapView;
    private MapboxMap mapboxMap;

    private static final String MONSTER_ICON_ID = "MONSTER_ICON_ID";
    private static final String CANDY_ICON_ID = "CANDY_ICON_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        startLoadingPage();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;
                checkFirstSession();
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
                Log.d("MyMain", "click: " + Objects.requireNonNull(symbol.getData()).toString());

                //TODO onClick: icona mostro --> activity combattimento con informazioni mostro (tramite intent)
                //TODO onClick: icona caramella --> activity rifornimento con informazioni caramella (tramite intent)

                return false;
            }
        });

        style.addImage(MONSTER_ICON_ID, Objects.requireNonNull(getDrawable(R.drawable.monster_marker_view)));
        style.addImage(CANDY_ICON_ID, Objects.requireNonNull(getDrawable(R.drawable.candy_marker_view)));

        List<SymbolOptions> monsterSymbolOptions = new ArrayList<>();
        List<SymbolOptions> candySymbolOptions = new ArrayList<>();

        try {
            for (int i = 0; i < mModel.getSizeMappa(); i++) {
                JSONObject mapObject = mModel.getElementoMappa(i);
                if (mapObject.getString("type").equals("MO")) {
                    monsterSymbolOptions.add(new SymbolOptions()
                            .withLatLng(new LatLng(mapObject.getDouble("lat"), mapObject.getDouble("lon")))
                            .withIconImage("MONSTER_ICON_ID")
                            .withData(new JsonParser().parse(mapObject.toString()).getAsJsonObject())); //da JSON a Json
                }
                else{
                    candySymbolOptions.add(new SymbolOptions()
                            .withLatLng(new LatLng(mapObject.getDouble("lat"), mapObject.getDouble("lon")))
                            .withIconImage("CANDY_ICON_ID")
                            .withData(new JsonParser().parse(mapObject.toString()).getAsJsonObject())); //da JSON a Json
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        symbolManager.create(monsterSymbolOptions);
        symbolManager.create(candySymbolOptions);

        //close loading page:
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
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
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

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

    void startLoadingPage(){
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        FragmentCaricamento schermataCaricamento = new FragmentCaricamento();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, schermataCaricamento);
        transaction.commit();
    }

    //TODO metodo animazione barra di caricamento --> fine rimando a schermata mappa

    // !!! MainActivity è la classe anche per la mappa !!!
    //TODO caricamento: informazioni giocatore
    //TODO caricamento: mostri e caramelle

    //TODO onClick: immagine utente --> activity profilo utente
    //TODO onClick: trofeo --> activity classifica



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
    protected void onSaveInstanceState(Bundle outState) {
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