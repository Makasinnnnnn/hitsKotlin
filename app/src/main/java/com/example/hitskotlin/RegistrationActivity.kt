package com.example.hitskotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegistrationActivity : AppCompatActivity() {
    private var step = 1
    private var name = ""
    private var gender = "male"
    private var login = ""
    private var email = ""
    private var birthDate: LocalDate? = null
    private var password = ""
    private var repeatedPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        if (savedInstanceState != null) {
            step = savedInstanceState.getInt("step", 1)
            name = savedInstanceState.getString("name").orEmpty()
            gender = savedInstanceState.getString("gender") ?: "male"
            login = savedInstanceState.getString("login").orEmpty()
            email = savedInstanceState.getString("email").orEmpty()
            birthDate = savedInstanceState.getString("birthDate")?.let(LocalDate::parse)
            password = savedInstanceState.getString("password").orEmpty()
            repeatedPassword = savedInstanceState.getString("repeatedPassword").orEmpty()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (step == 2) showDetails() else finish()
            }
        })

        if (step == 2) showPassword() else showDetails()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("step", step)
        outState.putString("name", name)
        outState.putString("gender", gender)
        outState.putString("login", login)
        outState.putString("email", email)
        outState.putString("birthDate", birthDate?.toString())
        outState.putString("password", password)
        outState.putString("repeatedPassword", repeatedPassword)
        super.onSaveInstanceState(outState)
    }

    private fun showDetails() {
        step = 1
        setContentView(R.layout.activity_registration_details)
        applySystemBarInsets()

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val loginInput = findViewById<EditText>(R.id.registrationLoginInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val genderGroup = findViewById<RadioGroup>(R.id.genderGroup)
        val birthDateField = findViewById<FrameLayout>(R.id.birthDateField)
        val birthDateText = findViewById<TextView>(R.id.birthDateText)
        val continueButton = findViewById<Button>(R.id.continueButton)

        nameInput.setText(name)
        loginInput.setText(login)
        emailInput.setText(email)
        genderGroup.check(if (gender == "female") R.id.femaleOption else R.id.maleOption)
        birthDateText.text = birthDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

        fun updateForm() {
            name = nameInput.text.toString()
            login = loginInput.text.toString()
            email = emailInput.text.toString()
            continueButton.isEnabled = AuthCheck.canContinueRegistration(
                name, login, email, birthDate != null
            )
            emailInput.error = if (email.isNotBlank() && !AuthCheck.isValidEmail(email)) {
                getString(R.string.email_error)
            } else {
                null
            }
        }

        nameInput.doAfterTextChanged { updateForm() }
        loginInput.doAfterTextChanged { updateForm() }
        emailInput.doAfterTextChanged { updateForm() }
        genderGroup.setOnCheckedChangeListener { _, checkedId ->
            gender = if (checkedId == R.id.femaleOption) "female" else "male"
        }

        birthDateField.setOnClickListener {
            val initialDate = birthDate ?: LocalDate.now()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    birthDate = LocalDate.of(year, month + 1, day)
                    birthDateText.text = birthDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    updateForm()
                },
                initialDate.year,
                initialDate.monthValue - 1,
                initialDate.dayOfMonth
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }

        continueButton.setOnClickListener { showPassword() }
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }
        findViewById<TextView>(R.id.signInLink).setOnClickListener { finish() }
        updateForm()
    }

    private fun showPassword() {
        step = 2
        setContentView(R.layout.activity_registration_password)
        applySystemBarInsets()

        val passwordInput = findViewById<EditText>(R.id.newPasswordInput)
        val repeatInput = findViewById<EditText>(R.id.repeatPasswordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        passwordInput.setText(password)
        repeatInput.setText(repeatedPassword)

        fun updateForm() {
            password = passwordInput.text.toString()
            repeatedPassword = repeatInput.text.toString()
            registerButton.isEnabled = AuthCheck.canRegister(password, repeatedPassword)
            repeatInput.error = if (
                repeatedPassword.isNotBlank() && password != repeatedPassword
            ) {
                getString(R.string.password_mismatch)
            } else {
                null
            }
        }

        passwordInput.doAfterTextChanged { updateForm() }
        repeatInput.doAfterTextChanged { updateForm() }
        setupPasswordVisibility(passwordInput, R.id.newPasswordVisibility)
        setupPasswordVisibility(repeatInput, R.id.repeatPasswordVisibility)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { showDetails() }
        findViewById<TextView>(R.id.signInLink).setOnClickListener { finish() }
        updateForm()
    }

    private fun setupPasswordVisibility(input: EditText, buttonId: Int) {
        val button = findViewById<ImageButton>(buttonId)
        button.setOnClickListener {
            val hidden = input.transformationMethod is PasswordTransformationMethod
            input.transformationMethod = if (hidden) null else PasswordTransformationMethod.getInstance()
            button.contentDescription = getString(
                if (hidden) R.string.hide_password else R.string.show_password
            )
            input.setSelection(input.text.length)
        }
    }

    private fun applySystemBarInsets() {
        val root = findViewById<View>(R.id.registrationRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        root.post { ViewCompat.requestApplyInsets(root) }
    }
}
