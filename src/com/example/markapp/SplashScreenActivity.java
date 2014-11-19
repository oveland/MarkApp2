package com.example.markapp;

import java.util.Timer;
import java.util.TimerTask;
  
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

public class SplashScreenActivity extends Activity{

	// Definimos la duración del splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        // Definimos la orientacion a vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Escondemos la barra de Titulo
        requestWindowFeature(Window.FEATURE_NO_TITLE);
  
        setContentView(R.layout.activity_splashscreen);
  
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
  
                // Llamamos a la Actividad principal de la aplicacion
                Intent mainIntent = new Intent().setClass(
                		SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
  
                // Cerramos la actividad para que el usuario no pueda 
                // volver a ella presionando el boton Atras
                finish();
            }
        };
  
        // Simulación de un proceso de carga largo al iniciar la aplicación.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }

}
