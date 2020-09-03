package com.example.mostridatasca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfiloUtente extends AppCompatActivity {

    private String SESSION_ID = "";
    private final static String URL_SET_PROFILE = "https://ewserver.di.unimi.it/mobicomp/mostri/setprofile.php";
    private static JSONObject AUTHENTICATION = new JSONObject();
    private ImageView immagineProfilo;
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
        //richiamo il metodo per inserire i dati del profilo
        setProfilo();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View vi = inflater.inflate(R.layout.dialog_mod_username, null);
        //classe anonima bottone modifica username
        final TextView usernameMod = vi.findViewById(R.id.usernameMod);
        Button btn_modNome = findViewById(R.id.btn_modNome);
        btn_modNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfiloUtente.this)
                        .setTitle("Modifica username")
                        .setView(vi)
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("BottoneModificaUsername", usernameMod.getText().toString());
                                modificaUsername(usernameMod.getText().toString());
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        //SELEZIONA FOTOCAMERA
        Button btn_mod_img = findViewById(R.id.btn_mod_img);
        btn_mod_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
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

    private void selectImage() {
        final CharSequence[] options = { "Fotocamera", "Galleria","Cancella" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfiloUtente.this);
        builder.setTitle("Selezione foto");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Fotocamera"))
                {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if (options[item].equals("Galleria"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Cancella")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    //metodo per inserire i dati utente nel profilo
    private void setProfilo(){
        immagineProfilo = findViewById(R.id.imgUtente);
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

    private void modificaUsername(final String username){
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
                        Model.getInstance().setUsername(username);
                        setProfilo();
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

    /*public void clickNew(View v)
    {
        //Toast.makeText(this, "Hai cliccato l'immagine.", Toast.LENGTH_LONG).show();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);


        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);

    }*/

    //GALLERIA E FOTOCAMERA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Log.d("fotocamera", "sei in 0");
                    // ricevo l'intent come parametro di ingresso
                    // ottengo il valore ritornato dall'intent
                    Bundle extras = imageReturnedIntent.getExtras();
                    // effettuo il casting in un oggetto di tipo Bitmap. Il valore è salvato sotto la chiave data
                    Bitmap mImageBitmap = (Bitmap) extras.get("data");
                    // carico l'immagine nell'oggetto ImageView presente nell'interfaccia grafica dell'app
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos); // bm is the bitmap object
                    byte[] byteArrayImage = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    Log.d("immagine", "base64: " + encodedImage);
                    //metodo
                    modificaImmagineProfilo(encodedImage);
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
                        Log.d("modificaImmagine", "Response: " + response.toString());

                        Model.getInstance().setImageGiocatore(img64);
                        setProfilo();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("modificaImmagine", "Error: " + error.toString());
            }
        }
        );
        mRequestQueue.add(request);
    }
}