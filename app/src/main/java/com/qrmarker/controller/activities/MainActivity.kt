package com.qrmarker.controller.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.volley.Request
import com.qrmarker.R
import com.qrmarker.models.BackEndConnection
import com.qrmarker.models.LoadingDialog
import com.qrmarker.models.Session
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
            if (email.text.isEmpty()) {
                email.requestFocus()
                email.error = resources.getString(R.string.empty_field_error)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()) {
                email.requestFocus()
                email.error = getString(R.string.invalid_email)
            }

            if (password.text.isEmpty()) {
                if (email.error == null) {
                    password.requestFocus()
                }
                password.error = resources.getString(R.string.empty_field_error)
            }

            if (email.error == null && email.text.isNotEmpty() && password.error == null && password.text.isNotEmpty()) {
                loadingDialog.show()
                val jsonObject = JSONObject()
                jsonObject.put("email", email.text.toString())
                jsonObject.put("password", password.text.toString())
                BackEndConnection(
                    this,
                    "logIn", Request.Method.POST, "auth/login", jsonObject, -1
                )
            }
        }

        findViewById<TextView>(R.id.create_account).setOnClickListener {
            loginForm.visibility = View.GONE
            createForm.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.register).setOnClickListener {
            if (fullName.text.isEmpty()) {
                fullName.requestFocus()
                fullName.error = resources.getString(R.string.empty_field_error)
            }

            if (phoneNumber.text.isEmpty()) {
                if (fullName.error == null) {
                    phoneNumber.requestFocus()
                }
                phoneNumber.error = resources.getString(R.string.empty_field_error)
            } else if (phoneNumber.text.length < 8) {
                if (fullName.error == null) {
                    phoneNumber.requestFocus()
                }
                phoneNumber.error = getString(R.string.invalid_phone_number)
            }

            if (createEmail.text.isEmpty()) {
                if (fullName.error == null && phoneNumber.error == null) {
                    createEmail.requestFocus()
                }
                createEmail.error = resources.getString(R.string.empty_field_error)
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(createEmail.text).matches()) {
                if (fullName.error == null && phoneNumber.error == null) {
                    createEmail.requestFocus()
                }
                createEmail.error = getString(R.string.invalid_email)
            }

            if (createPassword.text.isEmpty()) {
                if (fullName.error == null && phoneNumber.error == null && createEmail.error == null) {
                    createPassword.requestFocus()
                }
                createPassword.error = resources.getString(R.string.empty_field_error)
            } else if (createPassword.text.length < 8) {
                if (fullName.error == null && phoneNumber.error == null && createEmail.error == null) {
                    createPassword.requestFocus()
                }
                createPassword.error = getString(R.string.weak_password)
            }

            if (fullName.error == null && phoneNumber.error == null && createEmail.error == null && createPassword.error == null) {
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
        if (Session(this).encryptedToken() != "") {
            startActivity(Intent(this, Scanner::class.java))
            finish()
        }
    }

    fun loggedIn(l: Int, message: String) {
        loadingDialog.dismiss()
        if (l == 1) {
            hideKeyboard()
            startActivity(Intent(this, Scanner::class.java))
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
            hideKeyboard()
            startActivity(Intent(this, Scanner::class.java))
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

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}