package com.example.mostridatasca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfiloUtente extends AppCompatActivity {

    private String SESSION_ID = "";
    private final static String URL_SET_PROFILE = "https://ewserver.di.unimi.it/mobicomp/mostri/setprofile.php";
    private static JSONObject AUTHENTICATION = new JSONObject();
    private ImageButton immagineProfilo;
    private TextView username;
    private TextView vita;
    private TextView xp;
    private Uri outputFileUri;

    private JSONObject datiProfiloGiocatore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente);
        SESSION_ID = getString(R.string.test_session_id);
        datiProfiloGiocatore = Model.getInstance().getDatiGiocatore();

        immagineProfilo = findViewById(R.id.imgUtente);
        username = findViewById(R.id.username_profilo);
        vita = findViewById(R.id.lp_profilo);
        xp = findViewById(R.id.xp_profilo);

        //richiamo il metodo per inserire i dati del profilo
        setProfilo();

        //classe anonima bottone modifica username
        final TextView username = findViewById(R.id.username_profilo);
        Button btn_modNome = findViewById(R.id.btn_modNome);
        btn_modNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificaUsername(username.getText().toString());
                Log.d("BottoneModificaUsername", username.getText().toString());
            }
        });

        Button btnTorna = findViewById(R.id.btnTorna);
        btnTorna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
    //metodo per inserire i dati utente nel profilo
    private void setProfilo(){

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

    private void modificaUsername(String username){
        AUTHENTICATION = new JSONObject();
        try {
            AUTHENTICATION.put("session_id", SESSION_ID);
            AUTHENTICATION.put("username", username);
        }catch (JSONException e){
            e.printStackTrace();
        }
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                URL_SET_PROFILE,
                AUTHENTICATION,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("modificaUsername", "Response: " + response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("modificaUsername", "Error: " + error.toString());
            }
        }
        );
        mRequestQueue.add(request);
    }

    public void clickNew(View v)
    {
        //Toast.makeText(this, "Hai cliccato l'immagine.", Toast.LENGTH_LONG).show();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);

        /*
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
        */
    }

    //GALLERIA E FOTOCAMERA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Log.d("fotocamera", "sei in 0");
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = this.getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bm = BitmapFactory.decodeStream(imageStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 10, baos); // bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    Log.d("immagine", "base64: " + encodedImage);
                    //metodo
                    modificaImmagineProfilo(encodedImage);
                }
                break;
        }
    }

    public void modificaImmagineProfilo(final String img64){
        AUTHENTICATION = new JSONObject();
        try {
            AUTHENTICATION.put("session_id", SESSION_ID);
            AUTHENTICATION.put("img", img64);
        }catch (JSONException e){
            e.printStackTrace();
        }
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                URL_SET_PROFILE,
                AUTHENTICATION,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("modificaImmgine", "Response: " + response.toString());

                        Model.getInstance().setImageUtente(img64);
                        setProfilo();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("modificaImmgine", "Error: " + error.toString());
            }
        }
        );
        mRequestQueue.add(request);
    }
}