package com.example.mostridatasca;


import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView nomeGiocatore;
    private TextView xpGiocatore;

    public ViewHolder(View itemView){
        super(itemView);
        nomeGiocatore = itemView.findViewById(R.id.nome);
        xpGiocatore = itemView.findViewById(R.id.xp);
    }

    void setText(JSONObject datiObject){
        try {
            nomeGiocatore.setText(datiObject.getString("username"));
            xpGiocatore.setText(datiObject.getString("xp"));
            Log.d("ViewHolder", "Nome: " + datiObject.getString("username") + " Ex: " + datiObject.getString("xp"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
