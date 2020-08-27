package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfiloUtente extends AppCompatActivity {

    private final static String SESSION_ID = "8eOWKEcSInwRNBwC";
    private final static String URL_GET_RANKING = "https://ewserver.di.unimi.it/mobicomp/mostri/setprofile.php";
    private final static JSONObject AUTHENTICATION = new JSONObject();
    private ImageView immagineProfilo;
    private TextView username;
    private TextView vita;
    private TextView xp;

    private JSONObject datiProfiloGiocatore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente);

        datiProfiloGiocatore = Model.getInstance().getDatiGiocatore();

        //richiamo il metodo per inserire i dati del profilo
        setProfilo();

    }
    //metodo per inserire i dati utente nel profilo
    private void setProfilo(){
        immagineProfilo = findViewById(R.id.imgProfilo);
        username = findViewById(R.id.username_profilo);
        vita = findViewById(R.id.lp_profilo);
        xp = findViewById(R.id.xp_profilo);
        try {
            byte[] decodedString = Base64.decode(datiProfiloGiocatore.getString("img"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            immagineProfilo.setImageBitmap(decodedByte);
            username.setText(datiProfiloGiocatore.getString("username"));
            vita.setText(datiProfiloGiocatore.getString("lp"));
            xp.setText(datiProfiloGiocatore.getString("xp"));
            Log.d("ProfiloUtente", "Nome: " + datiProfiloGiocatore.getString("username") + " Ex: " + datiProfiloGiocatore.getString("xp"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}