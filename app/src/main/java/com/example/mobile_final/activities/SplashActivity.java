package com.example.mobile_final.activities;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_final.R;


public class SplashActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstance){
         super.onCreate(savedInstance);
         setContentView(R.layout.activity_splash);

         Button registerButton = findViewById(R.id.register);
         Button loginButton = findViewById(R.id.login);

         registerButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 Intent registerIntent = new Intent(SplashActivity.this , RegisterActivity.class);
                 startActivity(registerIntent);
             }
         });

         loginButton.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 Intent loginIntent = new Intent(SplashActivity.this , LoginActivity.class);
                 startActivity(loginIntent);
             }
         });
     }
}
