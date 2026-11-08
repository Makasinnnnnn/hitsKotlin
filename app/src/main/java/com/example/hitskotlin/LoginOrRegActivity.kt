package com.example.hitskotlin

import android.content.Intent
import com.example.hitskotlin.di.appContainer
import com.example.hitskotlin.ui.openCatalog
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class LoginOrRegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (appContainer.session.token.value != null) {
            openCatalog()
            return
        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_or_reg)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.choiceRoot)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        findViewById<Button>(R.id.choiceLoginButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        findViewById<Button>(R.id.choiceRegisterButton).setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
