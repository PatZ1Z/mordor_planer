package com.example.mordor

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinner = findViewById<Spinner>(R.id.spinner)

        val races = listOf(
            R.drawable.hobbit_icon,
            R.drawable.human_icon,
            R.drawable.elf_icon,
            R.drawable.krasnolod_icon,
            R.drawable.wizard_icon
        )

        // Lista statycznych elementów
        val items = listOf("Wybierz rasę bohatera", "hobbit", "człowiek", "elf","krasnolud","czarodziej")

        // Tworzenie adaptera
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Przypisanie adaptera do Spinnera
        spinner.adapter = adapter

        // Obsługa kliknięcia
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                val selectedItem = items[position]
                if (position != 0) { // Pomijamy "Wybierz opcję"
                    Toast.makeText(this@MainActivity, "Wybrano: $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nic nie wybrane
            }
        }
    }
}