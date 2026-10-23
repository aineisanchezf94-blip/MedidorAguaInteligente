package com.docente.medidoraguainteligente

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

/**
 * Ventana (Activity) de Métricas y Cálculo de Consumo de Agua.
 */
class MetricasActivity : AppCompatActivity() {

    private lateinit var tilMeterCode: TextInputLayout
    private lateinit var tilNumber1: TextInputLayout
    private lateinit var tilNumber2: TextInputLayout

    private lateinit var etMeterCode: EditText
    private lateinit var etNumber1: EditText
    private lateinit var etNumber2: EditText

    private lateinit var tvResult: TextView
    private lateinit var tvResultTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_metricas)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainMetricas)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        tilMeterCode = findViewById(R.id.tilMeterCode)
        tilNumber1 = findViewById(R.id.tilNumber1)
        tilNumber2 = findViewById(R.id.tilNumber2)

        etMeterCode = findViewById(R.id.etMeterCode)
        etNumber1 = findViewById(R.id.etNumber1)
        etNumber2 = findViewById(R.id.etNumber2)

        tvResult = findViewById(R.id.tvResult)
        tvResultTitle = findViewById(R.id.tvResultTitle)

        val btnProcessBilling = findViewById<MaterialButton>(R.id.btnProcessBilling)
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val btnMul = findViewById<Button>(R.id.btnMul)
        val btnDiv = findViewById<Button>(R.id.btnDiv)

        btnProcessBilling.setOnClickListener {
            if (validateFormInputs()) {
                processBillingCalculation()
            }
        }

        btnAdd.setOnClickListener { processOperation("+") }
        btnSub.setOnClickListener { processOperation("-") }
        btnMul.setOnClickListener { processOperation("*") }
        btnDiv.setOnClickListener { processOperation("/") }
    }

    private fun validateFormInputs(): Boolean {
        var isValid = true

        tilMeterCode.error = null
        tilNumber1.error = null
        tilNumber2.error = null

        val meterCode = etMeterCode.text.toString().trim()
        val input1Str = etNumber1.text.toString().trim()
        val input2Str = etNumber2.text.toString().trim()

        if (meterCode.isEmpty()) {
            tilMeterCode.error = "Error ISO-01: El código de medidor es obligatorio"
            isValid = false
        } else if (!meterCode.matches(Regex("^[A-Z0-9\\-]+$"))) {
            tilMeterCode.error = "Error ISO-02: Formato inválido. Solo mayúsculas, números y guiones"
            isValid = false
        }

        val currentVal = input1Str.toDoubleOrNull()
        if (input1Str.isEmpty()) {
            tilNumber1.error = "Error ISO-03: Ingrese la lectura actual"
            isValid = false
        } else if (currentVal == null) {
            tilNumber1.error = "Error ISO-04: Formato numérico no válido"
            isValid = false
        } else if (currentVal < 0.0) {
            tilNumber1.error = "Error ISO-05: La lectura no puede ser negativa"
            isValid = false
        } else if (currentVal > 99999.0) {
            tilNumber1.error = "Error ISO-06: Valor excede el límite máximo del medidor (99,999 m³)"
            isValid = false
        }

        val previousVal = input2Str.toDoubleOrNull()
        if (input2Str.isEmpty()) {
            tilNumber2.error = "Error ISO-07: Ingrese la lectura anterior"
            isValid = false
        } else if (previousVal == null) {
            tilNumber2.error = "Error ISO-08: Formato numérico no válido"
            isValid = false
        } else if (previousVal < 0.0) {
            tilNumber2.error = "Error ISO-09: La lectura no puede ser negativa"
            isValid = false
        }

        if (currentVal != null && previousVal != null && currentVal < previousVal) {
            tilNumber1.error = "Error ISO-10: La lectura actual debe ser mayor o igual a la lectura anterior"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Validación de seguridad fallida. Verifique los datos.", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun processBillingCalculation() {
        val meterCode = etMeterCode.text.toString().trim()
        val currentVal = etNumber1.text.toString().trim().toDouble()
        val previousVal = etNumber2.text.toString().trim().toDouble()

        val consumoM3 = currentVal - previousVal

        val subtotal: Double
        val rangoBase: Double
        val rangoIntermedio: Double
        val rangoExceso: Double

        if (consumoM3 <= 15.0) {
            rangoBase = consumoM3 * 0.50
            rangoIntermedio = 0.0
            rangoExceso = 0.0
            subtotal = rangoBase
        } else if (consumoM3 <= 30.0) {
            rangoBase = 15.0 * 0.50
            rangoIntermedio = (consumoM3 - 15.0) * 0.85
            rangoExceso = 0.0
            subtotal = rangoBase + rangoIntermedio
        } else {
            rangoBase = 15.0 * 0.50
            rangoIntermedio = 15.0 * 0.85
            rangoExceso = (consumoM3 - 30.0) * 1.50
            subtotal = rangoBase + rangoIntermedio + rangoExceso
        }

        val iva = subtotal * 0.15
        val totalPagar = subtotal + iva
        val securityToken = "SEC-${(100000..999999).random()}-X"

        tvResultTitle.text = "FACTURA TOTAL PROCESADA ($)"
        tvResult.text = String.format(Locale.US, "$%.2f", totalPagar)

        val reportMessage = """
            🔒 INFORME DE SEGURIDAD Y CÁLCULO PROCESADO
            
            • Medidor ID: $meterCode
            • Consumo Neto: ${String.format(Locale.US, "%.2f", consumoM3)} m³
            
            DESGLOSE DE TARIFA:
            - Rango Base (0 - 15 m³): $${String.format(Locale.US, "%.2f", rangoBase)}
            - Rango Intermedio (16 - 30 m³): $${String.format(Locale.US, "%.2f", rangoIntermedio)}
            - Rango Exceso (> 30 m³): $${String.format(Locale.US, "%.2f", rangoExceso)}
            -------------------------------------
            • Subtotal: $${String.format(Locale.US, "%.2f", subtotal)}
            • IVA (15%): $${String.format(Locale.US, "%.2f", iva)}
            • TOTAL A PAGAR: $${String.format(Locale.US, "%.2f", totalPagar)}
            
            Token de Verificación ISO: $securityToken
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Emisión de Factura Procesada")
            .setMessage(reportMessage)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Factura generada con éxito ($securityToken)", Toast.LENGTH_LONG).show()
            }
            .show()
    }

    private fun processOperation(operator: String) {
        val input1Str = etNumber1.text.toString().trim()
        val input2Str = etNumber2.text.toString().trim()

        if (input1Str.isEmpty() || input2Str.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese ambos valores", Toast.LENGTH_SHORT).show()
            return
        }

        val val1 = input1Str.toDoubleOrNull()
        val val2 = input2Str.toDoubleOrNull()

        if (val1 == null || val2 == null) {
            Toast.makeText(this, "Ingrese números válidos", Toast.LENGTH_SHORT).show()
            return
        }

        when (operator) {
            "+" -> {
                val res = val1 + val2
                tvResultTitle.text = "CONSUMO ESTIMADO TOTAL (m³)"
                tvResult.text = String.format(Locale.US, "%.2f", res)
            }
            "-" -> {
                val res = val1 - val2
                tvResultTitle.text = "DIFERENCIA DE CONSUMO (m³)"
                tvResult.text = String.format(Locale.US, "%.2f", res)
            }
            "*" -> {
                val res = val1 * val2
                tvResultTitle.text = "COSTO TOTAL ESTIMADO ($)"
                tvResult.text = String.format(Locale.US, "$%.2f", res)
            }
            "/" -> {
                if (val2 == 0.0) {
                    Toast.makeText(this, "No se puede dividir entre cero", Toast.LENGTH_SHORT).show()
                    tvResult.text = "ERR"
                    return
                }
                val res = val1 / val2
                tvResultTitle.text = "PROMEDIO CALCULADO (m³ / día)"
                tvResult.text = String.format(Locale.US, "%.2f", res)
            }
        }
    }
}
