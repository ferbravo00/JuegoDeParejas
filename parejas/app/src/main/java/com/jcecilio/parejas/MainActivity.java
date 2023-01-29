package com.jcecilio.parejas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    /*
        Creamos un array con los identificadores de los objetos ImageView
        Estos identificadores son simplemente enteros int
     */
    private static final int[] idArray = {R.id.img00,R.id.img01,R.id.img02,R.id.img03,R.id.img04,
            R.id.img10,R.id.img11,R.id.img12,R.id.img13,R.id.img14,
            R.id.img20,R.id.img21,R.id.img22,R.id.img23,R.id.img24,
            R.id.img30,R.id.img31,R.id.img32,R.id.img33,R.id.img34};

    /*
        Creamos un array con los identificadores de los drawable
     */
    private static final int[] idDrawable = {R.drawable._0_, R.drawable._1_, R.drawable._2_,
            R.drawable._3_, R.drawable._4_, R.drawable._5_, R.drawable._6_, R.drawable._7_,
            R.drawable._8_, R.drawable._9_ };

    /*
        Creamos un array con las posiciones de las cartas durante el juego
        Para ello, necesitamos un array del doble de longitud que el de las imágenes
        ya que cada carta está dos veces en el juego.
     */
    private static final int[] posicionesPartida = new int[idArray.length];

    /*
        Ahora, creamos un array de objetos ImageView
        de momento está vacío
     */
    private ImageView carta[] = new ImageView[idArray.length];

    // El siguiente array mantiene el estado de cada carta según esta tabla
    // 0 -> tapada
    // 1 -> destapada
    // 2 -> acertada
    private static int destapada[] = new int[20];

    private static int imagenAnteriorDestapada = 0;
    private static int posicionAnteriorDestapada = 99;
    private ImageView imagenViewAnteriorDestapada= null;

    public static int puntuacion = 0;
    private static int aciertos = 0;
    public static int pfinal = 0;
    private CountDownTimer temporizador;
    public static long sg = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button reiniciar = findViewById(R.id.button);
        final Button compartir = findViewById(R.id.button2);
        final TextView puntos = findViewById(R.id.textView);
        final TextView tiempo = findViewById(R.id.textView2);
        final TextView puntosFinal = findViewById(R.id.textView3);

        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barajea();
                iniZERO();
                inicia();
                compartir.setVisibility(View.INVISIBLE);
                puntuacion = 0;
                puntos.setText("Puntuacion: "+ puntuacion);
                puntosFinal.setText("");
                temporizador.start();
                for (int i = 0; i < carta.length; i++) {
                    carta[i].setClickable(true);
                }
            }
        });


        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MarcadorHTTP.class);
                startActivity(intent);
                MarcadorHTTP.cajaPuntosT.setText("hola");
            }
        });


        temporizador = new CountDownTimer(15000,1000){
            private boolean msg10sg = true;
            public void onTick(long millisUntilFinished){
                sg= millisUntilFinished / 1000;
                tiempo.setText("Tiempo: "+sg);
                if(millisUntilFinished < 11000 && msg10sg){
                    Toast.makeText(getApplicationContext(), "10 sg", Toast.LENGTH_SHORT).show();
                    msg10sg = false;
                }
            }
            public void onFinish(){
                tiempo.setText("Se acabó");
                for (int i = 0; i < carta.length; i++) {
                    carta[i].setClickable(false);
                }
                puntosFinal.setText("Puntuacion Total: "+puntuacion);
                compartir.setVisibility(View.VISIBLE);
            }
        }.start();

        // ocultar la barra de la aplicación
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        /*
            Parte de este código (el control de la matriz de objetos) ha sido adaptado del siguiente vídeo
            https://www.youtube.com/watch?v=7qykuBubpR4
         */

        /*
            sortear cartas de forma aleatoria
            en este ejemplo el orden no es aleatorio, si no este;
            01234
            56789
            98765
            43210
         */
        barajea();
        iniZERO();
        inicia();
        compartir.setVisibility(View.INVISIBLE);

        /*
            Bucle para el control del click
            ATENCIÓN a este bucle!!!!
         */
        for( int nn = 0; nn<idArray.length; nn++){
            carta[nn] = (ImageView)findViewById(idArray[nn]);
            carta[nn].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewMiCarta) {
                    // viewMiCarta es la carta (el ImageView) sobre la que estoy pulsando

                    int posEnArray = damePos(viewMiCarta.getId());
                    Toast.makeText(getApplicationContext(), String.valueOf(posEnArray), Toast.LENGTH_SHORT ).show();
                    // Toast.makeText(getApplicationContext(), String.valueOf(viewMiCarta.getId()), Toast.LENGTH_SHORT ).show();

                    if (cartasDestapadas() < 2) {
                        // atención aquí a los paréntesis
                        ((ImageView) viewMiCarta).setImageResource(posicionesPartida[posEnArray]);
                        destapada[posEnArray] = 1;

                        if (imagenAnteriorDestapada == posicionesPartida[posEnArray] && cartasDestapadas() ==2) {
                            new CountDownTimer(1000, 1000) {
                                public void onTick(long millisUntilFinished) {  }
                                public void onFinish() {
                                    ((ImageView) viewMiCarta).setImageResource(R.drawable._acierto_);
                                    //imagenViewAnteriorDestapada.setImageResource(R.drawable._acierto_);
                                }
                            }.start();

                            //((ImageView) viewMiCarta).setImageResource(R.drawable._acierto_);
                            imagenViewAnteriorDestapada.setImageResource(R.drawable._acierto_);
                            puntuacion = puntuacion +5;
                            aciertos++;
                            puntos.setText("Puntuacion: "+ puntuacion);
                            destapada[posEnArray] = 2;
                            destapada[posicionAnteriorDestapada] = 2;
                            if(aciertos==10){
                                temporizador.cancel();
                                pfinal=(int)sg*5+puntuacion;
                                puntosFinal.setText("Puntuacion Total: "+pfinal);
                                compartir.setVisibility(View.VISIBLE);
                            }
                        }

                        // almaceno el id de la imagen destapada. En este caso se refiere a la foto, es decir al valor que está mostrando
                        imagenAnteriorDestapada = posicionesPartida[posEnArray];
                        // almaceno la posición que estaba destapada
                        posicionAnteriorDestapada = posEnArray;
                        // almaceno el ImageView que estaba destapado... creo que esto lo puedo simplificar
                        imagenViewAnteriorDestapada = (ImageView) viewMiCarta;
                    }
                    else {      // si no hay menos de dos cartas destapadas se inicia() la baraja. Se refiere a q se tapan todas las cartas
                        inicia();
                        puntuacion = puntuacion -1;
                        puntos.setText("Puntuacion: "+ puntuacion);
                    }
                }
            });     // Cierre del método .setOnClickListener()
        }   // Cierre del bucle de control de los click
    }   // Cierre del método onCreate()

    // INICIO FUNCIONES USUARIO
    /*
        Función que barajea el mazo
        en primer lugar rellena el array con los ids de los drawables ordenados, y dos veces
        posteriormente intercambia parejas de cartas al azar. Recorre el array entero haciendo intercambios
        por lo que se realizan 20 intercambios lo que quiere decir que algunas cartas cambiaran
        más de una vez de posición
        posicionesPartida[]  = new int[idDrawable.length * 2];
        ATENCIÓN!!!!! ELTEXTO DE ARRIBA NO ESTÁ IMPLEMENTADO, DEBEIS RESSCRIBIR LA FUNCION VOSOTROS
        O USAR UNA NUEVA FUNCION QUE BARAJEE REALMENTE COMO DICE EL TEXTO DE ARRIBA
     */
    private void barajea(){
        // el tamaño de la baraja es 10... en este ejemplo
        // de momento no barajeo para saber donde están las parejas
        for(int j = 0; j < idDrawable.length; j++) {
            posicionesPartida[j] = idDrawable[j];
        }
        // relleno de la segunda mitad
        for(int j = 10; j < idDrawable.length *2; j++) {
            posicionesPartida[j] = idDrawable[19-j];
        }
    }

    /*

        // el tamaño de la baraja es 10... en este ejemplo
        // de momento no barajeo para saber donde están las parejas
        for(int j = 0; j < idDrawable.length; j++) {
            posicionesPartida[j] = idDrawable[j];
        }
        // relleno de la segunda mitad
        for(int j = 10; j < idDrawable.length *2; j++) {
            posicionesPartida[j] = idDrawable[19-j];
        }



        Función que devuelve la posición de la carta pulsada
        Se compara el id del ImageView recibido con cada uno de los almacenados
        en el array idArray
     */
    private int damePos(int pIdObjeto) {
        int ii = 0;
        while (pIdObjeto != idArray[ii++]);     // doy por hecho que encuentro el id... si no DESBORDAMIENTO
        return ii-1;
    }

    /*
        devuelve el número de cartas destapadas
     */
    private int cartasDestapadas(){
        int contador = 0;
        for(int ii = 0; ii < destapada.length; ii++){
            if (destapada[ii] ==1) contador++;
        }
        return contador;
    }

    /*
        En realidad inica o reinicia las cartas del reverso
     */
    private void inicia(){
        for(int n = 0; n < 20; n++) {
            if (destapada[n] == 1) {
                carta[n] = (ImageView) findViewById(idArray[n]);    // accedo a cada objeto ImageView mediante su id previamente almacenado en idArray[]
                carta[n].setImageResource(R.drawable.rever00);      // establezco en cada ImageView (carta[n]) la imagen del reverso
                destapada[n] = 0;
            }
        }
    }

    /*
        Pone el array destapada todos a 1
        Como el layout está construido con las imágenes de las cartas destapadas... primero relleno la matriz de estado con 1
        para que la función inicia() las tape (cargando la imagen del corcho)
     */
    private void iniZERO(){
        for(int n = 0; n < 20; n++) destapada[n] = 1;
    }

}   // Cierre de la clase MainActivity