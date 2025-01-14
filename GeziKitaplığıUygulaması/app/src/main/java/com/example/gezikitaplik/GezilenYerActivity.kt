package com.example.gezikitaplik

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class GezilenYerActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezilen_yerler)

        // DatabaseHelper'ı başlatıyoruz
        dbHelper = DatabaseHelper(this)

        // Gelen ID'yi alıyoruz
        val gezilenYerId = intent.getStringExtra("gezilenYerId")?.toInt() // Burada ID'yi string olarak alıyoruz, sonra integer'a dönüştürüyoruz
        if (gezilenYerId != null) {
            // ID'ye göre gezilen yerin detaylarını alıyoruz
            val gezilenYer = dbHelper.getGezilenYerById(gezilenYerId)

            // Detayları ekranda göstermek için gerekli TextView'leri ayarlıyoruz
            val tvTitle = findViewById<TextView>(R.id.tvTitle)
            val tvDescription = findViewById<TextView>(R.id.tvDescription)
            val tvLocation = findViewById<TextView>(R.id.tvLocation)
            val imageView = findViewById<ImageView>(R.id.imageView)

            // Veritabanından gelen bilgileri ekrana yazıyoruz
            tvTitle.text = gezilenYer?.gezilenYer ?: "Bilinmiyor"
            tvDescription.text = gezilenYer?.aciklama ?: "Açıklama yok"
            tvLocation.text = gezilenYer?.resimUri ?: "Konum bilgisi yok"

            // Eğer varsa, resmi Glide ile yükleyelim
            gezilenYer?.resimUri?.let {
                Glide.with(this).load(it).into(imageView)
            }
        }
    }
}
