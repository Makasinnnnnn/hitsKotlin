package com.example.hitskotlin

import android.os.Bundle
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginInput = findViewById<EditText>(R.id.loginInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val passwordVisibilityButton = findViewById<ImageButton>(R.id.passwordVisibilityButton)

        fun updateLoginButton() {
            loginButton.isEnabled = AuthCheck.canSubmitLogin(
                loginInput.text.toString(),
                passwordInput.text.toString()
            )
        }

        loginInput.doAfterTextChanged { updateLoginButton() }
        passwordInput.doAfterTextChanged { updateLoginButton() }

        passwordVisibilityButton.setOnClickListener {
            val passwordIsHidden = passwordInput.transformationMethod is PasswordTransformationMethod
            passwordInput.transformationMethod =
                if (passwordIsHidden) null else PasswordTransformationMethod.getInstance()
            passwordVisibilityButton.contentDescription = getString(
                if (passwordIsHidden) R.string.hide_password else R.string.show_password
            )
            passwordInput.setSelection(passwordInput.text.length)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }

        val registerPrompt = findViewById<TextView>(R.id.registerPrompt)
        val promptText = getString(R.string.register_prompt)
        val linkText = getString(R.string.register_link)
        val linkStart = promptText.indexOf(linkText)
        if (linkStart >= 0) {
            registerPrompt.text = SpannableString(promptText).apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.accent_pink)),
                    linkStart,
                    linkStart + linkText.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        registerPrompt.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

}
