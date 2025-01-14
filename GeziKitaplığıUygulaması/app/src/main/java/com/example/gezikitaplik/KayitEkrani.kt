package com.example.gezikitaplik

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class KayitEkrani : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kayit_ekrani)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val tvBackToLogin = findViewById<TextView>(R.id.tv_back_to_login)

        // Kayıt ol butonuna tıklama
        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // Kullanıcı adı kontrolü
            if (username.isEmpty()) {
                Toast.makeText(this, "Kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // E-posta doğrulama kontrolü
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Geçerli bir e-posta adresi giriniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Şifre onayı kontrolü
            if (password != confirmPassword) {
                Toast.makeText(this, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Veritabanı işlemleri
            val dbHelper = DatabaseHelper(this)

            if (dbHelper.usernameExists(username)) {
                Toast.makeText(this, "Bu kullanıcı adı zaten alınmış.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kullanıcıyı veritabanına ekliyoruz
            val result = dbHelper.insertUser(username, email, password)

            // Kullanıcı başarıyla eklendi mi kontrol et
            if (result != -1L) {
                Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()

                // GirisEkrani'ne yönlendir
                val intent = Intent(this, GirisEkrani::class.java)
                startActivity(intent)
                finish()  // Kaydın ardından bu ekranı bitir
            } else {
                Toast.makeText(this, "Kayıt sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }
        }

        // Geri dönmek için login ekranına yönlendir
        tvBackToLogin.setOnClickListener {
            val intent = Intent(this, GirisEkrani::class.java) // Giriş ekranına yönlendirme
            startActivity(intent)
            finish()
        }
    }
}
