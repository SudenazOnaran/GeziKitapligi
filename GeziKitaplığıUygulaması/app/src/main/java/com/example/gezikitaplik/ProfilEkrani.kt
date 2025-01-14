package com.example.gezikitaplik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class ProfilEkrani : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profil_ekrani)

        // Ekran kenar boşluklarını ayarlama
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = DatabaseHelper(this)

        // SharedPreferences'tan giriş yapan kullanıcının bilgilerini alıyoruz
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)

        // TextView'leri buluyoruz
        val tvUserName = findViewById<TextView>(R.id.profile_name)
        val tvUserEmail = findViewById<TextView>(R.id.profile_email)

        // Kullanıcı bilgilerini ekranda göstermek
        if (username != null && email != null) {
            tvUserName.text = "Kullanıcı Adı: $username"
            tvUserEmail.text = "E-posta: $email"
        } else {
            tvUserName.text = "Kullanıcı Adı bulunamadı"
            tvUserEmail.text = "E-posta bulunamadı"
        }



        // Geri Butonu
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Profil Düzenleme Butonu
        val editButton: Button = findViewById(R.id.edit_profile_button)
        editButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Çıkış Yapma Butonu
        val logoutButton: TextView = findViewById(R.id.logout)
        logoutButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }



        // BottomNavigationView için navigasyon işlemi
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {

                    if (username != null) {
                        // Kullanıcı adını TextView'de gösteriyoruz
                        val tvUserName = findViewById<TextView>(R.id.tvUserName)
                        tvUserName.text = "Hello, $username!"
                    }
                    val intent = Intent(this, anasayfa::class.java)
                    startActivity(intent)
                    true

                }

                R.id.nav_profile -> true
                R.id.nav_add -> {
                    val intent = Intent(this, NewShare::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
