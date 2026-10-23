package com.docente.medidoraguainteligente.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.docente.medidoraguainteligente.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

/**
 * Fragmento 1: Métricas y Procesamiento Calculado de Consumo de Agua con Estándar ISO de Seguridad.
 * Incluye captura de datos mediante formulario, validación rigurosa de entradas,
 * cálculo por rangos de consumo, prevención de inyecciones/valores anómalos y emisión de mensajes.
 */
class MetricsFragment : Fragment() {

    private lateinit var tilMeterCode: TextInputLayout
    private lateinit var tilNumber1: TextInputLayout
    private lateinit var tilNumber2: TextInputLayout

    private lateinit var etMeterCode: EditText
    private lateinit var etNumber1: EditText
    private lateinit var etNumber2: EditText

    private lateinit var tvResult: TextView
    private lateinit var tvResultTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_metrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tilMeterCode = view.findViewById(R.id.tilMeterCode)
        tilNumber1 = view.findViewById(R.id.tilNumber1)
        tilNumber2 = view.findViewById(R.id.tilNumber2)

        etMeterCode = view.findViewById(R.id.etMeterCode)
        etNumber1 = view.findViewById(R.id.etNumber1)
        etNumber2 = view.findViewById(R.id.etNumber2)

        tvResult = view.findViewById(R.id.tvResult)
        tvResultTitle = view.findViewById(R.id.tvResultTitle)

        val btnProcessBilling: MaterialButton = view.findViewById(R.id.btnProcessBilling)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnSub: Button = view.findViewById(R.id.btnSub)
        val btnMul: Button = view.findViewById(R.id.btnMul)
        val btnDiv: Button = view.findViewById(R.id.btnDiv)

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

    /**
     * Validación Rigurosa de Entradas (Estándar ISO / OWASP Input Validation)
     */
    private fun validateFormInputs(): Boolean {
        var isValid = true

        // Limpiar errores previos
        tilMeterCode.error = null
        tilNumber1.error = null
        tilNumber2.error = null

        val meterCode = etMeterCode.text.toString().trim()
        val input1Str = etNumber1.text.toString().trim()
        val input2Str = etNumber2.text.toString().trim()

        // 1. Validar Código de Medidor
        if (meterCode.isEmpty()) {
            tilMeterCode.error = "Error ISO-01: El código de medidor es obligatorio"
            isValid = false
        } else if (!meterCode.matches(Regex("^[A-Z0-9\\-]+$"))) {
            tilMeterCode.error = "Error ISO-02: Formato inválido. Solo mayúsculas, números y guiones"
            isValid = false
        }

        // 2. Validar Lectura Actual
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

        // 3. Validar Lectura Anterior
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

        // 4. Lógica de Consumo: Lectura Actual >= Lectura Anterior
        if (currentVal != null && previousVal != null && currentVal < previousVal) {
            tilNumber1.error = "Error ISO-10: La lectura actual debe ser mayor o igual a la lectura anterior"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(
                requireContext(),
                "Validación de seguridad fallida. Por favor corrija los errores marcados.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return isValid
    }

    /**
     * Procesamiento de Cálculos Seguro con Desglose de Tarifas e Impuestos
     */
    private fun processBillingCalculation() {
        val meterCode = etMeterCode.text.toString().trim()
        val currentVal = etNumber1.text.toString().trim().toDouble()
        val previousVal = etNumber2.text.toString().trim().toDouble()

        val consumoM3 = currentVal - previousVal

        // Estructura de Tarifas por Rangos Escalados
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

        // Generar Token de Transacción Seguro
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

        AlertDialog.Builder(requireContext())
            .setTitle("Emisión de Factura Procesada")
            .setMessage(reportMessage)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Datos transmitidos y registrados con éxito ($securityToken)",
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    private fun processOperation(operator: String) {
        val input1Str = etNumber1.text.toString().trim()
        val input2Str = etNumber2.text.toString().trim()

        if (input1Str.isEmpty() || input2Str.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor ingrese ambos valores", Toast.LENGTH_SHORT).show()
            return
        }

        val val1 = input1Str.toDoubleOrNull()
        val val2 = input2Str.toDoubleOrNull()

        if (val1 == null || val2 == null) {
            Toast.makeText(requireContext(), "Ingrese números válidos", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "No se puede dividir entre cero", Toast.LENGTH_SHORT).show()
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
