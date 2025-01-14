package com.example.gezikitaplik

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GirisEkrani : AppCompatActivity() {

    // DatabaseHelper sınıfını tanımlıyoruz
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_giris_ekrani) // Giriş ekranı layout'u set ediliyor

        // Email ve şifre giriş alanlarını layout'tan referans alıyoruz
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)

        // Giriş butonunu layout'tan referans alıyoruz
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // Veritabanı yardımıcı sınıfını başlatıyoruz
        databaseHelper = DatabaseHelper(this)

        // SharedPreferences başlatılıyor, kullanıcı bilgilerini tutmak için
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Giriş butonuna tıklama olayını tanımlıyoruz
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim() // Kullanıcının girdiği e-posta alınır ve boşluklar kırpılır
            val password = etPassword.text.toString() // Kullanıcının girdiği şifre alınır

            // E-posta ve şifre doğrulama işlemleri
            when {
                email.isEmpty() || password.isEmpty() -> { // Eğer alanlardan biri boşsa
                    Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { // E-posta geçerli formatta değilse
                    Toast.makeText(this, "Geçerli bir e-posta adresi giriniz!", Toast.LENGTH_SHORT).show()
                }
                databaseHelper.checkUser(email, password) -> { // Veritabanında kullanıcı doğrulanırsa
                    val username = databaseHelper.getUsernameByEmail(email) // Kullanıcının kullanıcı adı alınır
                    val userEmail = email // E-posta değişkene atanır

                    // Giriş başarılı, kullanıcı bilgilerini SharedPreferences'a kaydediyoruz
                    editor.putString("username", username) // Kullanıcı adını kaydediyoruz
                    editor.putString("email", userEmail) // Kullanıcı e-postasını kaydediyoruz
                    editor.apply() // Değişiklikleri kaydeder

                    Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show() // Kullanıcıya başarı mesajı gösterilir

                    // Anasayfaya yönlendirme işlemi
                    val intent = Intent(this, anasayfa::class.java) // Anasayfa aktivitesini başlatıyoruz
                    intent.putExtra("username", username) // Kullanıcı adını intent'e ekliyoruz
                    intent.putExtra("email", userEmail) // Kullanıcı e-postasını intent'e ekliyoruz
                    startActivity(intent) // Anasayfa aktivitesini başlatır
                    finish() // Bu aktiviteyi kapatır, geri dönülemez
                }
                else -> {
                    // Kullanıcı bulunamazsa veya şifre yanlışsa hata mesajı gösterilir
                    Toast.makeText(this, "E-posta veya şifre hatalı!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Kaydol butonuna tıklama olayını işleyen metod
    fun kaydol(view: View) {
        // Kullanıcıyı kayıt ekranına yönlendirme işlemi
        val intent = Intent(this, KayitEkrani::class.java) // Kayıt ekranı aktivitesini başlatır
        startActivity(intent) // Aktiviteyi başlatır
    }
}
