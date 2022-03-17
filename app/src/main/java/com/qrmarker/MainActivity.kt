package com.qrmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    private lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setTheme(R.style.Theme_QRMarker)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        password = findViewById(R.id.password)

        findViewById<Button>(R.id.log_in).setOnClickListener {
            if (password.text.isNotEmpty()) {
                Session(this).password(password.text.toString())
                startActivity(Intent(this, Scanner::class.java))
            } else {
                password.requestFocus()
                password.setSelection(password.text.length)
                password.error = resources.getString(R.string.empty_field_error)
            }
        }
    }
}