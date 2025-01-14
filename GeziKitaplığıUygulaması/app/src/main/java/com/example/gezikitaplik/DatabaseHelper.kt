package com.example.gezikitaplik

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.gezikitaplik.model.GezilenYer
import android.database.Cursor



class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MobilDenemeDataBase.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys = ON;")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Kullanıcılar tablosunu oluşturuyoruz
        val createDenemeTableQuery = """
            CREATE TABLE Deneme (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT NOT NULL,
                sifre TEXT NOT NULL
            )
        """
        db?.execSQL(createDenemeTableQuery)

        // Gezilen Yerler tablosunu oluşturuyoruz
        val createGezilenYerlerTableQuery = """
            CREATE TABLE GezilenYerler (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                resimUri TEXT,
                aciklama TEXT,
                gezilenYer TEXT,
                userId INTEGER NOT NULL,
                FOREIGN KEY (userId) REFERENCES Deneme (id) ON DELETE CASCADE
            )
        """
        db?.execSQL(createGezilenYerlerTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS GezilenYerler")
        db?.execSQL("DROP TABLE IF EXISTS Deneme")
        onCreate(db)
    }

    // Yeni kullanıcı eklemek için fonksiyon
    fun insertUser(username: String,email: String, sifre: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("email", email)
        contentValues.put("sifre", sifre)

        return try {
            db.insertOrThrow("Deneme", null, contentValues)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting user: ${e.message}")
            -1L
        }
    }
    fun updatePassword(userId: Int, newPassword: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("sifre", newPassword)  // "sifre" sütununu güncelliyoruz

        // Güncelleme işlemi
        val rowsUpdated = db.update("Deneme", contentValues, "id = ?", arrayOf(userId.toString()))

        return rowsUpdated
    }
    // Kullanıcı adının var olup olmadığını kontrol etme
    fun usernameExists(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM Deneme WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Kullanıcıyı kontrol etme fonksiyonu
    fun checkUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM Deneme WHERE email = ? AND sifre = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Gezilen yerler tablosuna veri ekleme fonksiyonu
    fun insertGezilenYer(resimUri: String, aciklama: String, gezilenYer: String, userId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("resimUri", resimUri)
        contentValues.put("aciklama", aciklama)
        contentValues.put("gezilenYer", gezilenYer)
        contentValues.put("userId", userId)

        return db.insert("GezilenYerler", null, contentValues)
    }

    // Gezilen yerleri listeleme
    fun getAllGezilenYerler(): List<GezilenYer> {
        val gezilenYerlerList = mutableListOf<GezilenYer>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM GezilenYerler", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val resimUri = cursor.getString(cursor.getColumnIndexOrThrow("resimUri"))
                val aciklama = cursor.getString(cursor.getColumnIndexOrThrow("aciklama"))
                val gezilenYer = cursor.getString(cursor.getColumnIndexOrThrow("gezilenYer"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"))

                val gezilenYerObj = GezilenYer(id = id, // id'yi veritabanından alıyoruz
                    resimUri = resimUri,
                    aciklama = aciklama,
                    gezilenYer = gezilenYer,
                    userId = userId)

                gezilenYerlerList.add(gezilenYerObj)

            } while (cursor.moveToNext())
        }
        cursor.close()
        return gezilenYerlerList
    }

    // Email ile kullanıcı adı alma
    fun getUsernameByEmail(email: String): String? {
        val db = readableDatabase
        val query = "SELECT username FROM Deneme WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        var username: String? = null

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("username")
            if (columnIndex != -1) {
                username = cursor.getString(columnIndex)
            }
        }
        cursor.close()
        return username
    }
    // Kullanıcı adı ile email alma
    fun getEmailByUsername(username: String): String? {
        val db = readableDatabase
        val query = "SELECT email FROM Deneme WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        var email: String? = null
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        }
        cursor.close()
        return email
    }


    // Kullanıcı adı ile email alma
    // Bu fonksiyon, verilen e-posta adresine göre kullanıcı ID'sini döndürecektir.
    // DatabaseHelper.kt

    fun getUserIdByEmail(email: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM Deneme WHERE email = ?", arrayOf(email))

        var userId = -1

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("id")
            if (columnIndex != -1) {
                userId = cursor.getInt(columnIndex)
            }
        }
        cursor.close()
        return userId
    }

    // Kullanıcıya ait gezilen yerleri listeleme
    fun getUserSharedLocations(userId: Int): List<GezilenYer> {
        val postList = mutableListOf<GezilenYer>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT id, gezilenYer, resimUri, aciklama, userId FROM GezilenYerler WHERE userId = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                // Sütun indekslerini kontrol et
                val idIndex = cursor.getColumnIndex("id")
                val gezilenYerIndex = cursor.getColumnIndex("gezilenYer")
                val resimUriIndex = cursor.getColumnIndex("resimUri")
                val aciklamaIndex = cursor.getColumnIndex("aciklama")
                val userIdIndex = cursor.getColumnIndex("userId")

                // Eğer sütun indeksi -1 ise, o sütun veritabanında yok demektir.
                if (idIndex >= 0 && gezilenYerIndex >= 0 && resimUriIndex >= 0 && aciklamaIndex >= 0 && userIdIndex >= 0) {
                    val id = cursor.getInt(idIndex)
                    val gezilenYer = cursor.getString(gezilenYerIndex)
                    val resimUri = cursor.getString(resimUriIndex)
                    val aciklama = cursor.getString(aciklamaIndex)
                    val userId = cursor.getInt(userIdIndex)

                    // GezilenYer nesnesini oluşturuyoruz ve listeye ekliyoruz
                    val gezilenYerObj = GezilenYer(id, resimUri, aciklama, gezilenYer, userId)
                    postList.add(gezilenYerObj)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return postList
    }



    fun getGezilenYerById(id: Int): GezilenYer? {
        val db = this.readableDatabase
        var gezilenYer: GezilenYer? = null
        val cursor = db.query(
            "GezilenYerler", // Tablo ismi
            null, // Tüm sütunlar
            "id = ?", // Sorgu
            arrayOf(id.toString()), // ID'yi filtrele
            null, null, null
        )

        // Eğer cursor null değilse ve veriler varsa, veri alıp GezilenYer objesi oluşturuyoruz
        cursor?.let {
            if (it.moveToFirst()) {
                val resimUri = it.getString(it.getColumnIndexOrThrow("resimUri"))
                val aciklama = it.getString(it.getColumnIndexOrThrow("aciklama"))
                val gezilenYerName = it.getString(it.getColumnIndexOrThrow("gezilenYer"))
                val userId = it.getInt(it.getColumnIndexOrThrow("userId"))

                gezilenYer = GezilenYer(id, resimUri, aciklama, gezilenYerName, userId)
            }
            it.close() // cursor'u kapatıyoruz
        }
        return gezilenYer
    }

}