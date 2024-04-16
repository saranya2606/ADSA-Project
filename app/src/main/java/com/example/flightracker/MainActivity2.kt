package com.example.flightracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    private lateinit var airportDetailsContainer: LinearLayout
    private lateinit var sourceEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    private var numAirports: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.airports_details_page)

        numAirports = intent.getIntExtra("numAirports", 0)

        airportDetailsContainer = findViewById(R.id.airportDetailsContainer)
        sourceEditText = findViewById(R.id.sourceEditText)
        destinationEditText = findViewById(R.id.destinationEditText)
        calculateButton = findViewById(R.id.calculateButton)
        resultTextView = findViewById(R.id.resultTextView)

        val connections = mutableListOf<Int>()

        // Dynamically generate UI components for airport details
        /*for (i in 0 until numAirports) {
            val airportLayout = layoutInflater.inflate(R.layout.item_airport, null) as LinearLayout
            val connectionsEditText = airportLayout.findViewById<EditText>(R.id.connectionsEditText)
            airportDetailsContainer.addView(airportLayout)
            //connections.add(connectionsEditText.text.toString().toInt())
        }*/
        for (i in 0 until numAirports) {
            // Inflate the airport layout
            val airportLayout = layoutInflater.inflate(R.layout.item_airport, null) as LinearLayout
            airportDetailsContainer.addView(airportLayout)

            // Retrieve the number of connections for this airport from user input (assuming user enters it)
            val numConnections = numAirports// get the number of connections from user input (e.g., EditText)

            // Inflate the airport details layout for each connection
            for (j in 0 until numConnections) {
                val airportDetailsLayout =
                    layoutInflater.inflate(R.layout.item_airport_details, null) as LinearLayout
                val destinationAirportSpinner =
                    airportDetailsLayout.findViewById<Spinner>(R.id.destinationAirportSpinner)
                val weightEditText =
                    airportDetailsLayout.findViewById<EditText>(R.id.weightEditText)

                // Dynamically generate airport names array based on numAirports
                val airportNames = Array(numAirports) { i -> "Airport ${i + 1}" }

                // Create and set adapter for the spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, airportNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                destinationAirportSpinner.adapter = adapter

                // Add the airport details layout to the airport layout
                airportLayout.addView(airportDetailsLayout)

                // Add a tag to the airport details layout for identification
                airportDetailsLayout.tag = j
            }

                // Inner loop for creating dropdown boxes based on number of connections
                /*for (j in 0 until numAirports) {
                // Create the spinner dynamically
                val connectionDropdown = Spinner(this)
                val connectionAdapter = ArrayAdapter.createFromResource(this, R.array.airport_names, android.R.layout.simple_spinner_item)
                connectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                connectionDropdown.adapter = connectionAdapter

                // Add the spinner to the airport layout
                airportLayout.addView(connectionDropdown)

                // Create the corresponding weight EditText dynamically
                val weightEditText = EditText(this)
                weightEditText.hint = "Weight for Connection $j"
                airportLayout.addView(weightEditText)
            }*/

        }





        calculateButton.setOnClickListener {
            val sourceAirport = sourceEditText.text.toString().toInt()
            val destinationAirport = destinationEditText.text.toString().toInt()

            if (sourceAirport < 0 || sourceAirport >= numAirports ||
                destinationAirport < 0 || destinationAirport >= numAirports
            ) {
                Toast.makeText(this, "Invalid source or destination airport", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val airportDetails = mutableListOf<Pair<Int, List<Pair<Int, Int>>>>()

            /*for (i in 0 until numAirports) {
                val airportLayout = layoutInflater.inflate(R.layout.item_airport_details, null) as LinearLayout
                val destinationAirportSpinner = airportLayout.findViewById<Spinner>(R.id.destinationAirportSpinner)
                val weightEditText = airportLayout.findViewById<EditText>(R.id.weightEditText)

                val adapter = ArrayAdapter.createFromResource(this, R.array.airport_names, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                destinationAirportSpinner.adapter = adapter

                airportDetailsContainer.addView(airportLayout)

                airportLayout.tag = i
            }*/

            for (i in 0 until numAirports) {
                val airportLayout = airportDetailsContainer.getChildAt(i) as LinearLayout
                val destinationAirportSpinner = airportLayout.findViewById<Spinner>(R.id.destinationAirportSpinner)
                val weightEditText = airportLayout.findViewById<EditText>(R.id.weightEditText)

                val destination = destinationAirportSpinner.selectedItemPosition
                val weight = weightEditText.text.toString().toInt()

                airportDetails.add(i to listOf(destination to weight))
            }

            val graph = createGraph(numAirports, airportDetails)
            val (shortestPath, distance) = graph.dijkstra(sourceAirport, destinationAirport)

            displayResult(shortestPath, distance)
        }
    }

    private fun createGraph(numAirports: Int, airportDetails: List<Pair<Int, List<Pair<Int, Int>>>>): Graph {
        val graph = Graph(numAirports)
        for ((source, connections) in airportDetails) {
            for ((destination, weight) in connections) {
                graph.addEdge(source, destination, weight)
            }
        }
        return graph
    }

    private fun displayResult(shortestPath: List<Int>, distance: Int) {
        resultTextView.text = "Shortest path: ${shortestPath.joinToString(" -> ")}\nTotal distance: $distance km"
        resultTextView.visibility = View.VISIBLE
    }
}
