package com.example.micrud;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Referencia al ImageView
        ImageView imageViewGif = findViewById(R.id.imageViewGif);

        // Cargar el GIF usando Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.logo) // logo.gif en res/drawable
                .into(imageViewGif);

        // Temporizador para cambiar de activity después del splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar MainActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                // Finalizar SplashActivity
                finish();
            }
        }, SPLASH_DURATION);
    }
}