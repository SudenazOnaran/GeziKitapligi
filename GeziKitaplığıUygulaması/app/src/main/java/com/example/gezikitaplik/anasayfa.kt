package com.example.gezikitaplik

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import com.example.gezikitaplik.model.GezilenYer

class anasayfa : AppCompatActivity() {

    // DatabaseHelper sınıfı, veritabanı işlemlerini yönetir
    private lateinit var dbHelper: DatabaseHelper

    // Activity oluşturulduğunda çalışacak metod
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge tasarımını etkinleştirir, ekran kenarlarına kadar uygulamanın yayılmasını sağlar
        enableEdgeToEdge()

        // Ana sayfa layout'u ayarlanır
        setContentView(R.layout.anasayfa)

        // Veritabanı yardımcı sınıfını başlatıyoruz
        dbHelper = DatabaseHelper(this)


        // Sistem çubuğuna (status bar, nav bar) göre ekranın kenarlarına padding ekler
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Sistem çubuklarından alınan boşlukları alır
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Bu boşlukları ana görünümün padding'ine uygular
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Kullanıcı e-posta bilgisi Intent'ten alınır
        val userEmail = intent.getStringExtra("email")

        // Kullanıcı adı Intent'ten alınır
        val username = intent.getStringExtra("username")

        // Kullanıcı adı null değilse, TextView'e kullanıcı adı yerleştirilir
        if (username != null) {
            val tvUserName = findViewById<TextView>(R.id.tvUserName)
            tvUserName.text = "Hello, $username!"  // Kullanıcı adı TextView'de görüntülenir
        }

        // BottomNavigationView öğesine tıklama olaylarını tanımlıyoruz
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Tıklama olayları yönetilir
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Profil menüsüne tıklandığında ProfilEkrani'ne geçiş yapılır
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfilEkrani::class.java)
                    if (username != null) {
                        // Kullanıcı adı profil ekranına gönderilir
                        intent.putExtra("username", username)
                        intent.putExtra("email", userEmail)
                    }
                    startActivity(intent)  // Profil ekranına geçiş yapılır
                    true
                }
                // Yeni paylaşım ekranına tıklandığında NewShare ekranına geçiş yapılır
                R.id.nav_add -> {
                    val intent = Intent(this, NewShare::class.java)
                    if (username != null) {
                        // Kullanıcı adı NewShare ekranına gönderilir
                        intent.putExtra("username", username)
                    }
                    startActivity(intent)  // NewShare ekranına geçiş yapılır
                    true
                }
                else -> false
            }
        }

        // Gezilen yerlerin listeleneceği ListView bağlantısı yapılır
        val listView: ListView = findViewById(R.id.gezilenYerListView)

        // Veritabanından gezilen yerler alınır.degiskene atanır.
        val gezilenYerler = dbHelper.getAllGezilenYerler()

        // Gezilen yerleri listView'e bağlamak için bir adapter kullanılır
        val adapter = GezilenYerAdapter(this, gezilenYerler)
        listView.adapter = adapter  // ListView'e adapter atanır

        // ListView'deki öğelere tıklama olayları eklenir
        listView.setOnItemClickListener { _, _, position, _ ->
            // Tıklanan gezilen yer alınır
            val gezilenYer = gezilenYerler[position]

            // Tıklanan gezilen yerin ID'si ve resim URI'si alınır
            val gezilenYerId = gezilenYer.id
            val resimUri = gezilenYer.resimUri

            // Detay sayfasına geçiş yapılır
            val intent = Intent(this, GezilenYerDetailActivity::class.java)
            intent.putExtra("gezilenYerId", gezilenYerId)  // ID gönderilir
            intent.putExtra("resimUri", resimUri)  // Resim URI'si gönderilir

            startActivity(intent)  // Detay sayfasına geçiş başlatılır
        }

        // SearchView ile arama yapılacak
        val searchView = findViewById<SearchView>(R.id.searchView)

        // SearchView'in metin değişim olaylarını dinleme
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Kullanıcı sorguyu tamamladığında (search tuşuna bastığında) filtreleme yapılır
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)  // Adaptör filtreleme yapılır
                return false
            }

            // Kullanıcı yazdıkça filtreleme yapılır
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)  // Adaptör filtreleme yapılır
                return false
            }
        })
    }
}