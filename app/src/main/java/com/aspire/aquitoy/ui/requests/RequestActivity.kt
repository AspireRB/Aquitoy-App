package com.aspire.aquitoy.ui.requests

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aspire.aquitoy.R

class RequestActivity : AppCompatActivity() {

    private lateinit var textViewName: TextView
    private lateinit var textViewAge: TextView
    private lateinit var textViewCedula: TextView
    private lateinit var textViewFecha: TextView
    private lateinit var textViewNameNurse: TextView
    private lateinit var textViewCedulaNurse: TextView
    private lateinit var textViewMedicalDiagnosis: TextView
    private lateinit var textViewCurrentMedications: TextView
    private lateinit var textViewMedicalHistory: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clinic_history)

        // Obtener datos del intent
        val patientName = intent.getStringExtra("patientName")
        val patientAge = intent.getStringExtra("patientAge")
        val patientCedula = intent.getStringExtra("patientCedula")
        val fecha = intent.getStringExtra("patientFechaNacimiento")
        val nurseName = intent.getStringExtra("nurseName")
        val nurseTarjeta = intent.getStringExtra("nurseTarjeta")
        val medicalDiagnosis = intent.getStringExtra("medicalDiagnosis")
        val currentMedications = intent.getStringExtra("currentMedications")
        val medicalHistory = intent.getStringExtra("medicalHistory")

        // Inicializar las vistas
        textViewName = findViewById(R.id.textViewName)
        textViewAge = findViewById(R.id.textViewAge)
        textViewCedula = findViewById(R.id.textViewCedula)
        textViewFecha = findViewById(R.id.textViewFecha)
        textViewNameNurse = findViewById(R.id.textViewNameNurse)
        textViewCedulaNurse = findViewById(R.id.textViewCedulaNurse)
        textViewMedicalDiagnosis = findViewById(R.id.textViewMedicalDiagnosis)
        textViewCurrentMedications = findViewById(R.id.textViewCurrentMedications)
        textViewMedicalHistory = findViewById(R.id.textViewMedicalHistory)

        // Mostrar los datos en las vistas
        textViewName.text = patientName
        textViewAge.text = patientAge
        textViewCedula.text = patientCedula
        textViewFecha.text = fecha
        textViewNameNurse.text = nurseName
        textViewCedulaNurse.text = nurseTarjeta
        textViewMedicalDiagnosis.text = medicalDiagnosis
        textViewCurrentMedications.text = currentMedications
        textViewMedicalHistory.text = medicalHistory
        // Agrega aquí más asignaciones para mostrar toda la información del servicio

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { navigateToRequest() }
    }

    private fun navigateToRequest() {
        finish()
    }
}
