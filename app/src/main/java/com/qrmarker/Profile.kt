package com.qrmarker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.full_name).text = Session(this).fullName()
        findViewById<TextView>(R.id.email).text = Session(this).email()
        findViewById<TextView>(R.id.type).text = Session(this).userType()
        findViewById<TextView>(R.id.phone_number).text = Session(this).phoneNumber()

        findViewById<Button>(R.id.log_out).setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(false)
            dialog.setTitle(getString(R.string.confirm_log_out_title))
            dialog.setMessage(getString(R.string.confirm_log_out_message))
            dialog.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                Session(this).token("")
                Session(this).fullName("")
                Session(this).email("")
                Session(this).userType("")
                Session(this).phoneNumber("")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            dialog.setNegativeButton(resources.getString(R.string.no)) { d, _ ->
                d.dismiss()
            }
            dialog.show()
        }
    }
}