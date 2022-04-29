package com.qrmarker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.volley.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var fullName: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var createEmail: EditText
    private lateinit var createPassword: EditText
    private lateinit var loginForm: LinearLayout
    private lateinit var createForm: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setTheme(R.style.Theme_QRMarker)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingDialog = LoadingDialog(this)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        fullName = findViewById(R.id.full_name)
        phoneNumber = findViewById(R.id.phone)
        createEmail = findViewById(R.id.create_email)
        createPassword = findViewById(R.id.create_password)
        loginForm = findViewById(R.id.login_form)
        createForm = findViewById(R.id.create_form)

        findViewById<Button>(R.id.log_in).setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                loadingDialog.show()
                val jsonObject = JSONObject()
                jsonObject.put("email", email.text.toString())
                jsonObject.put("password", password.text.toString())
                BackEndConnection(
                    this,
                    "logIn", Request.Method.POST, "auth/login", jsonObject, -1
                )
            } else {
                password.requestFocus()
                password.setSelection(password.text.length)
                password.error = resources.getString(R.string.empty_field_error)
            }
        }

        findViewById<TextView>(R.id.create_account).setOnClickListener {
            loginForm.visibility = View.GONE
            createForm.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.register).setOnClickListener {
            var error = ""
            if (fullName.text.isEmpty()) {
                fullName.requestFocus()
                fullName.error = resources.getString(R.string.empty_field_error)
                error = getString(R.string.error)
            }

            if (phoneNumber.text.isNotEmpty()) {
                if (phoneNumber.text.length < 8) {
                    phoneNumber.requestFocus()
                    phoneNumber.error = getString(R.string.invalid_phone_number)
                    error = getString(R.string.error)
                }
            } else {
                phoneNumber.requestFocus()
                phoneNumber.error = resources.getString(R.string.empty_field_error)
                error = getString(R.string.error)
            }

            if (createEmail.text.isNotEmpty()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(createEmail.text).matches()) {
                    createEmail.requestFocus()
                    createEmail.error = getString(R.string.invalid_email)
                    error = getString(R.string.error)
                }
            } else {
                createEmail.requestFocus()
                createEmail.error = resources.getString(R.string.empty_field_error)
                error = getString(R.string.error)
            }

            if (createPassword.text.isNotEmpty()) {
                if (createPassword.text.length < 8) {
                    createPassword.requestFocus()
                    createPassword.error = getString(R.string.weak_password)
                    error = getString(R.string.error)
                }
            } else {
                createPassword.requestFocus()
                createPassword.error = resources.getString(R.string.empty_field_error)
                error = getString(R.string.error)
            }

            if (error == "") {
                loadingDialog.show()
                val jsonObject = JSONObject()
                jsonObject.put("fullName", fullName.text.toString())
                jsonObject.put("phoneNumber", phoneNumber.text.toString())
                jsonObject.put("email", createEmail.text.toString())
                jsonObject.put("password", createPassword.text.toString())
                BackEndConnection(
                    this,
                    "register", Request.Method.POST, "auth/register", jsonObject, -1
                )
            }
        }

        findViewById<TextView>(R.id.login).setOnClickListener {
            createForm.visibility = View.GONE
            loginForm.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if (Session(this).token() != "") {
            startActivity(Intent(this, Scanner::class.java))
            finish()
        }
    }

    fun loggedIn(l: Int, message: String) {
        loadingDialog.dismiss()
        if (l == 1) {
            startActivity(Intent(this, Scanner::class.java))
            Toast.makeText(this, getString(R.string.log_in_successful), Toast.LENGTH_LONG).show()
            finish()
        } else {
            if (message == "") {
                Toast.makeText(this, resources.getString(R.string.log_in_failed), Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun registered(l: Int, message: String) {
        loadingDialog.dismiss()
        if (l == 1) {
            startActivity(Intent(this, Scanner::class.java))
            Toast.makeText(this, getString(R.string.registered_successfully), Toast.LENGTH_LONG)
                .show()
            finish()
        } else {
            if (message == "") {
                Toast.makeText(
                    this,
                    resources.getString(R.string.registration_failed),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}