package com.app.helthcare

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val nameEditText = findViewById<EditText>(R.id.patient_name)
        val submitButton = findViewById<Button>(R.id.BtnSubmit)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            if (name.isNotEmpty()) {
                // Save the username
                with(sharedPreferences.edit()) {
                    putString("username", name)
                    apply()
                }

                // Start MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
