package com.example.mostridatasca;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView nomeGiocatore;
    private TextView xpGiocatore;
    private TextView posizioneInClassifica;
    private ImageView immagineProfilo;

    public ViewHolder(View itemView){
        super(itemView);
        immagineProfilo = itemView.findViewById(R.id.img);
        nomeGiocatore = itemView.findViewById(R.id.nome);
        xpGiocatore = itemView.findViewById(R.id.xp);
        posizioneInClassifica = itemView.findViewById(R.id.posizioneClassifica);
    }

    void setText(JSONObject datiObject, int position){
        try {
            posizioneInClassifica.setText("" + (position+1));
            byte[] decodedString = Base64.decode(datiObject.getString("img"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            immagineProfilo.setImageBitmap(decodedByte);
            nomeGiocatore.setText(datiObject.getString("username"));
            xpGiocatore.setText(datiObject.getString("xp"));
            Log.d("ViewHolder", "Nome: " + datiObject.getString("username") + " Ex: " + datiObject.getString("xp"));
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
