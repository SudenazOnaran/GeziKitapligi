package com.example.gezikitaplik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Ekran kenarlarına kadar uzanmayı sağlamak

        setContentView(R.layout.activity_main)


        // Butona tıklama olayını ekleyelim
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            // Yeni aktiviteye geçiş yapmak için Intent kullanıyoruz
            val intent = Intent(this, GirisEkrani::class.java) // GirisEkrani yerine doğru sınıf ismi
            startActivity(intent)
            finish()
        }

        // WindowInsetsListener yalnızca bir kez tanımlanmalı
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets // Sistem çubuklarının düzgün şekilde ayarlanmasını sağlar
        }


    }



}
