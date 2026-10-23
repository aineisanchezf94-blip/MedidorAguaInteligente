package com.docente.medidoraguainteligente

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.docente.medidoraguainteligente.auth.AuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activity Principal de la Aplicación Medidor de Agua Inteligente (HydroGuard).
 * Verifica la sesión de usuario y contiene enlaces a las 3 ventanas independientes.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        // Verificación de Autenticación ISO: Si no hay sesión activa, redirigir a LoginActivity
        if (!authManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Mostrar usuario autenticado
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        tvUserEmail.text = authManager.getUserEmail()

        // Botón de Cerrar Sesión
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Desea salir de la aplicación HydroGuard y cerrar la sesión activa?")
                .setPositiveButton("Cerrar Sesión") { _, _ ->
                    authManager.logout()
                    Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                    redirectToLogin()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Configurar Enlaces / Tarjetas a cada ventana
        val cardLinkMetricas: View = findViewById(R.id.cardLinkMetricas)
        val cardLinkServicios: View = findViewById(R.id.cardLinkServicios)
        val cardLinkAlertas: View = findViewById(R.id.cardLinkAlertas)

        val btnOpenMetricas: View = findViewById(R.id.btnOpenMetricas)
        val btnOpenServicios: View = findViewById(R.id.btnOpenServicios)
        val btnOpenAlertas: View = findViewById(R.id.btnOpenAlertas)

        val openMetricasLambda = { startActivity(Intent(this, MetricasActivity::class.java)) }
        val openServiciosLambda = { startActivity(Intent(this, ServiciosActivity::class.java)) }
        val openAlertasLambda = { startActivity(Intent(this, AlertasActivity::class.java)) }

        cardLinkMetricas.setOnClickListener { openMetricasLambda() }
        btnOpenMetricas.setOnClickListener { openMetricasLambda() }

        cardLinkServicios.setOnClickListener { openServiciosLambda() }
        btnOpenServicios.setOnClickListener { openServiciosLambda() }

        cardLinkAlertas.setOnClickListener { openAlertasLambda() }
        btnOpenAlertas.setOnClickListener { openAlertasLambda() }

        // Configurar Menú de Navegación Inferior
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_metrics -> {
                    openMetricasLambda()
                    true
                }
                R.id.nav_services -> {
                    openServiciosLambda()
                    true
                }
                R.id.nav_alerts -> {
                    openAlertasLambda()
                    true
                }
                else -> false
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
