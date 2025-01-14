package com.example.gezikitaplik

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editTextPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var databaseHelper: DatabaseHelper
    private var userId: Int = -1// Varsayılan olarak geçersiz ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // SharedPreferences ile e-posta bilgisini alıyoruz
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        // E-posta bilgisi boşsa, hata mesajı göster ve aktiviteyi sonlandır
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
            finish()  // Aktiviteyi sonlandır
            return
        }

        // DatabaseHelper'ı başlatıyoruz
        databaseHelper = DatabaseHelper(this)

        // E-posta bilgisinden kullanıcı ID'sini alıyoruz
        userId = databaseHelper.getUserIdByEmail(email)

        // EditText ve Button bileşenlerine referans alıyoruz
        editTextPassword = findViewById(R.id.editTextPassword)
        btnSave = findViewById(R.id.btnSave)

        // Save butonuna tıklama olayı
        btnSave.setOnClickListener {
            val newPassword = editTextPassword.text.toString()

            // Şifreyi kontrol et  Şifre boş değilse güncelleme işlemini başlat
            if (newPassword.isNotEmpty()) {
                val success = updatePassword(newPassword)
                if (success) {
                    // Güncelleme başarılı, kullanıcıya mesaj göster ve aktiviteyi kapat
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    finish() // Şifre güncellenince aktiviteyi sonlandır
                } else {// Güncelleme başarısız, hata mesajı göster
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                }
            } else {// Şifre alanı boşsa kullanıcıya uyarı göster
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
            }
        }

        // Geri Butonu
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    // Şifreyi veritabanında güncelleyen fonksiyon
    private fun updatePassword(password: String): Boolean {
        return try {// DatabaseHelper üzerinden şifreyi güncelle
            val updated = databaseHelper.updatePassword(userId, password)
            updated > 0 // Update işlemi başarılıysa > 0 döner
        } catch (e: Exception) { // Hata oluşursa false döner
            false
        }
    }
}
