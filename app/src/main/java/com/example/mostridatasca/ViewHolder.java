package com.example.mostridatasca;


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

    void setText(JSONObject datiObjet){
        try {
            nomeGiocatore.setText(datiObjet.getString("username"));
            xpGiocatore.setText(datiObjet.getString("xp"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
