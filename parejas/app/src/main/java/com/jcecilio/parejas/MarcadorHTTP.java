package com.jcecilio.parejas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MarcadorHTTP extends AppCompatActivity {

    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marcador_http);


        // ocultar la barra de la aplicación
        if (getSupportActionBar() != null)  getSupportActionBar().hide();

        final Button btEnviar = findViewById(R.id.btnEnviar);
        final EditText cajaJugador =  findViewById(R.id.eTxtJugador);
        final EditText cajaPuntosT =  findViewById(R.id.eTxtPT);
        final EditText cajaPuntosJ =  findViewById(R.id.eTxtPJ);
        final EditText cajaBonus =  findViewById(R.id.eTxtBonus);
        final EditText cajaTiempo =  findViewById(R.id.eTxtTiempo);
        final TextView msjERROR = findViewById(R.id.txtMSJerror);

        cajaPuntosJ.setText(String.valueOf(MainActivity.puntuacion));
        cajaPuntosT.setText(String.valueOf(MainActivity.pfinal));
        cajaBonus.setText(String.valueOf((int)MainActivity.sg*5));
        cajaTiempo.setText((String.valueOf(120-(int)MainActivity.sg)));

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // servidor php que procesa el POST
                String url = "https://digitalgentilis.com/apps/android/actualiza.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                        // response -> Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show(),
                        response -> msjERROR.setText(response),
                        error -> Toast.makeText(MarcadorHTTP.this, error.toString(), Toast.LENGTH_LONG).show()){
                    // añadir los parametros de la respuesta

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError{
                        Map<String, String> params = new HashMap<>();
                        params.put("JU", cajaJugador.getText().toString());
                        params.put("PT", cajaPuntosT.getText().toString());
                        params.put("PJ", cajaPuntosJ.getText().toString());
                        params.put("BO", cajaBonus.getText().toString());
                        params.put("TI", cajaTiempo.getText().toString());
                        return params;
                    }
                };
                requestQueue = Volley.newRequestQueue(MarcadorHTTP.this);
                requestQueue.add(stringRequest);
            }
        });
    }
}