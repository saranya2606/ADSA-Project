package com.example.flightracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var numAirportsEditText: EditText
    private lateinit var addAirportsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        numAirportsEditText = findViewById(R.id.numAirportsEditText)
        addAirportsButton = findViewById(R.id.addAirportsButton)

        addAirportsButton.setOnClickListener {
            val numAirports = numAirportsEditText.text.toString().toInt()
            val intent = Intent(this, MainActivity2::class.java)
            intent.putExtra("numAirports", numAirports)
            startActivity(intent)
        }
    }
}
