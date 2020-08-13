/*
 * Combattimento o fuga da mostro
 */

package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Combattimento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combattimento);
    }

    //TODO caricamento info mostro (img [chiamata: getImage], dimens, nome)

    //TODO onClick: cambattimento --> chiamata fighteat, fragment risultato
    //TODO onClick: scappa --> torna alla mappa

    //TODO fragment risultato combattimento (vittoria/sconfitta): xp e lp
    //TODO fragment onClick: torna alla mappa
}