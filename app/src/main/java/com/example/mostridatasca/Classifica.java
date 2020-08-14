/*
 * Tabella classifica primi 20 giocatori
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

public class Classifica extends AppCompatActivity {

    private final static String URL_GET_RANKING = "https://ewserver.di.unimi.it/mobicomp/mostri/ranking.php";
    private final static JSONObject AUTHENTICATION = new JSONObject();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);

        //TODO scaricare dati da server e caricarli nel model


    }

    //TODO caricamento: dati giocatori
}