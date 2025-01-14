package com.example.gezikitaplik

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class NewShare : AppCompatActivity() {

    // Görsel, açıklama, şehir bilgileri ve butonlar için değişkenler tanımlanıyor
    private lateinit var imageView: ImageView
    private lateinit var etDescription: EditText
    private lateinit var etCity: EditText
    private lateinit var btnShare: Button
    private lateinit var btnBack: ImageButton
    private var selectedImageUri: Uri? = null // Seçilen görselin URI'si
    private val dbHelper = DatabaseHelper(this) // Veritabanı yardımcısı sınıfı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_share)

        // Arayüz bileşenlerine referans alınıyor
        imageView = findViewById(R.id.selected_image)
        etDescription = findViewById(R.id.et_description)
        etCity = findViewById(R.id.et_city)
        btnShare = findViewById(R.id.btn_add_content)
        btnBack = findViewById(R.id.back_button)

        // Fotoğraf seçme işlemini başlatmak için bir "ActivityResultContract" kaydediliyor
        val getImage =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                selectedImageUri = uri // Seçilen görsel URI kaydediliyor
                imageView.setImageURI(uri) // Görsel, ImageView üzerinde gösteriliyor
            }

        // ImageView'e tıklanınca galeri açılıyor
        imageView.setOnClickListener {
            getImage.launch("image/*") // Sadece görüntü dosyaları gösteriliyor
        }

        // Paylaşım ekleme butonuna tıklama olayı
        btnShare.setOnClickListener {
            val description = etDescription.text.toString() // Açıklama metni
            val city = etCity.text.toString() // Şehir metni

            // Tüm alanlar doldurulmuş mu kontrol ediliyor
            if (selectedImageUri != null && description.isNotEmpty() && city.isNotEmpty()) {
                val userId = 1 // Örnek kullanıcı ID (Giriş yapan kullanıcıdan alınabilir)
                val uriString = selectedImageUri.toString() // Görselin URI'si string olarak kaydediliyor

                // Veritabanına yeni paylaşım ekleniyor
                val result = dbHelper.insertGezilenYer(uriString, description, city, userId)

                if (result > 0) {
                    // Paylaşım başarıyla eklendi, kullanıcıya mesaj gösteriliyor
                    Toast.makeText(this, "Paylaşım başarıyla eklendi!", Toast.LENGTH_SHORT).show()

                    // Kullanıcı adını intent ile alıp gösteriyoruz
                    val username = intent.getStringExtra("username")
                    if (username != null) {
                        val tvUserName = findViewById<TextView>(R.id.tvUserName)
                        tvUserName.text = "Hello, $username!"
                    }

                    // Başarılı paylaşım sonrası anasayfa ekranına geçiliyor
                    setContentView(R.layout.anasayfa)

                } else {
                    // Paylaşım eklenemezse hata mesajı gösteriliyor
                    Toast.makeText(this, "Bir hata oluştu.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Alanlardan biri eksikse kullanıcı uyarılıyor
                Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
            }
        }

        // Geri butonuna tıklama olayı
        btnBack.setOnClickListener {
            // Anasayfaya yönlendirme işlemi
            val intent = Intent(this, anasayfa::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Önceki aktiviteleri temizle
            startActivity(intent) // Anasayfa aktivitesini başlat
            finish() // Bu aktiviteyi kapat
        }
    }
}
