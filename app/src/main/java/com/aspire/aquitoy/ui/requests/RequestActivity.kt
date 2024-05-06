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
    private lateinit var textViewMedicalHistory: TextView
    private lateinit var editTextCurrentMedications: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clinic_history)

        // Obtener datos del intent
        val patientName = intent.getStringExtra("patientName")
        val patientAge = intent.getStringExtra("patientAge")
        val patientCedula = intent.getStringExtra("patientCedula")
        val fecha = intent.getStringExtra("fecha")
        val nurseName = intent.getStringExtra("nurseName")
        val nurseCedula = intent.getStringExtra("nurseCedula")
        val medicalHistory = intent.getStringExtra("medicalHistory")
        val currentMedications = intent.getStringExtra("currentMedications")

        // Inicializar las vistas
        textViewName = findViewById(R.id.textViewName)
        textViewAge = findViewById(R.id.textViewAge)
        textViewCedula = findViewById(R.id.textViewCedula)
        textViewFecha = findViewById(R.id.textViewFecha)
        textViewNameNurse = findViewById(R.id.textViewNameNurse)
        textViewCedulaNurse = findViewById(R.id.textViewCedulaNurse)
        textViewMedicalHistory = findViewById(R.id.textViewMedicalHistory)
        editTextCurrentMedications = findViewById(R.id.editTextCurrentMedications)

        // Mostrar los datos en las vistas
        textViewName.text = patientName
        textViewAge.text = patientAge
        textViewCedula.text = patientCedula
        textViewFecha.text = fecha
        textViewNameNurse.text = nurseName
        textViewCedulaNurse.text = nurseCedula
        textViewMedicalHistory.text = medicalHistory
        editTextCurrentMedications.text = currentMedications
        // Agrega aquí más asignaciones para mostrar toda la información del servicio

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { navigateToRequest() }
    }

    private fun navigateToRequest() {
        finish()
    }
}
