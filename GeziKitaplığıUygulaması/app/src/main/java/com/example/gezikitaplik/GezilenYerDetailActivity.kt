package com.example.gezikitaplik

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gezikitaplik.model.GezilenYer
class GezilenYerDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gezilen_yer_detail)

        // Intent ile gelen id'yi ve resim URI'sini alıyoruz
        val gezilenYerId = intent.getIntExtra("gezilenYerId", -1)
        val resimUri = intent.getStringExtra("resimUri")

        // Veritabanından gezilen yerin detaylarını alıyoruz
        val dbHelper = DatabaseHelper(this)
        val gezilenYer = dbHelper.getGezilenYerById(gezilenYerId)

        // Detayları gösteriyoruz
        val gezilenYerTextView: TextView = findViewById(R.id.gezilenYerDetailTextView)
        val aciklamaTextView: TextView = findViewById(R.id.aciklamaDetailTextView)
        val resimImageView: ImageView = findViewById(R.id.resimDetailImageView)

        gezilenYer?.let {
            gezilenYerTextView.text = it.gezilenYer
            aciklamaTextView.text = it.aciklama

            // Glide ile resim yükleme
            if (!resimUri.isNullOrEmpty()) {
                Glide.with(this)
                    .load(resimUri)
                    .placeholder(R.drawable.placeholder_image) // Yedek resim
                    .error(R.drawable.error_image) // Hata durumunda gösterilecek resim
                    .into(resimImageView)
            } else {
                resimImageView.setImageResource(R.drawable.placeholder_image)
            }
        }

        // Geri butonunun tıklama işlemi
        val geriButonu: ImageButton = findViewById(R.id.geriButonu)
        geriButonu.setOnClickListener {
            val intent = Intent(this, anasayfa::class.java)
            startActivity(intent)
            finish() // GezilenYerDetailActivity'yi kapatıyoruz
        }
    }
}
