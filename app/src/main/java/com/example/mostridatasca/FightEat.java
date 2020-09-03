/*
 * Combattimento o fuga da mostro
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FightEat extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private JSONObject dataFromSymbol;

    private ImageView image;
    private TextView name;
    private TextView size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fighteat);

        sharedPref = getSharedPreferences("preferenze", Context.MODE_PRIVATE);

        image = findViewById(R.id.imageView);
        name = findViewById(R.id.name);
        size = findViewById(R.id.sizeView);

        Intent intent = getIntent();
        String stringData = intent.getStringExtra("data");

        loadingWheel(true);

        try {
            dataFromSymbol = new JSONObject(stringData);

            loadImage(dataFromSymbol.getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button scappaBtn = findViewById(R.id.scappaView);
        scappaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button combattiBtn = findViewById(R.id.combattiView);
        combattiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    fightEat(dataFromSymbol.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button tornaBtn = findViewById(R.id.torna);
        tornaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        //vuoto di proposito
    }

    void fightEat(String target_id){
        String session_id = sharedPref.getString("session_id", "");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("session_id", session_id);
            jsonData.put("target_id", target_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest fightRequest = new JsonObjectRequest(
                getString(R.string.url_fight_eat),
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MyMain", response.toString());

                        TextView result = findViewById(R.id.result);
                        result.setText(response.toString());

                        afterFightEat();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyMain", error.toString());
                    }
        }
        );
        queue.add(fightRequest);
    }

    void loadImage(String target_id){
        String session_id = sharedPref.getString("session_id", "");

        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("session_id", session_id);
            jsonData.put("target_id", target_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest getImageRequest = new JsonObjectRequest(
                getString(R.string.url_get_image),
                jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String imgBase64 = "";

                        try {
                            imgBase64 = response.getString("img");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        image.setImageBitmap(decodedByte);

                        try {
                            name.setText(dataFromSymbol.getString("name"));
                            size.setText(dataFromSymbol.getString("size"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingWheel(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyMain", "error: " + error.toString());
                    }
                }
        );

        queue.add(getImageRequest);

    }

    void loadingWheel(boolean start){
        if (start){
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }

    void afterFightEat(){
        findViewById(R.id.afterFightEat).setVisibility(View.VISIBLE);
    }

    //TODO caricamento info mostro (img [chiamata: getImage], dimens, nome)

    //TODO onClick: cambattimento --> chiamata fighteat, fragment risultato
    //TODO onClick: scappa --> torna alla mappa

    //TODO fragment risultato combattimento (vittoria/sconfitta): xp e lp
    //TODO fragment onClick: torna alla mappa
}