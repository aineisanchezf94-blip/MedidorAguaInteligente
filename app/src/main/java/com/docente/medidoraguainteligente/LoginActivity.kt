package com.docente.medidoraguainteligente

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.docente.medidoraguainteligente.auth.AuthManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

/**
 * Pantalla de Autenticación de Usuarios.
 * Valida formato de correo, fortaleza de contraseña y token de medidor vinculado.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilMeterToken: TextInputLayout

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etMeterToken: EditText

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        // Si ya existe una sesión activa, ir directamente a MainActivity
        if (authManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLogin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilMeterToken = findViewById(R.id.tilMeterToken)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etMeterToken = findViewById(R.id.etMeterToken)

        val btnLogin: MaterialButton = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            if (validateLoginInputs()) {
                performLogin()
            }
        }
    }

    /**
     * Validación Rigurosa de Credenciales (Estándar de Seguridad)
     */
    private fun validateLoginInputs(): Boolean {
        var isValid = true

        tilEmail.error = null
        tilPassword.error = null
        tilMeterToken.error = null

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val meterToken = etMeterToken.text.toString().trim()

        // 1. Validar Correo Electrónico
        if (email.isEmpty()) {
            tilEmail.error = "Error Auth-01: El correo electrónico es obligatorio"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Error Auth-02: Formato de correo electrónico no válido"
            isValid = false
        }

        // 2. Validar Contraseña
        if (password.isEmpty()) {
            tilPassword.error = "Error Auth-03: Ingrese su contraseña"
            isValid = false
        } else if (password.length < 6) {
            tilPassword.error = "Error Auth-04: La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }

        // 3. Validar Token ID de Medidor
        if (meterToken.isEmpty()) {
            tilMeterToken.error = "Error Auth-05: El token del medidor es obligatorio"
            isValid = false
        } else if (!meterToken.matches(Regex("^[A-Z0-9\\-]+$"))) {
            tilMeterToken.error = "Error Auth-06: Formato de token inválido (Mayúsculas, números y guiones)"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Por favor corrija los datos ingresados.", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val meterToken = etMeterToken.text.toString().trim()

        // Guardar la sesión segura
        authManager.saveSession(email, meterToken)

        Toast.makeText(this, "¡Autenticación exitosa! Bienvenido $email", Toast.LENGTH_SHORT).show()
        navigateToMain()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Finalizar LoginActivity para que el usuario no vuelva atrás al presionar Back
    }
}
