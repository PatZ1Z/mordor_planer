package com.example.mordor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var raceIcon: ImageView
    private lateinit var dateTimeText: TextView
    private lateinit var summaryText: TextView
    private lateinit var timerProgressBar: ProgressBar
    private lateinit var chronometer: Chronometer
    private var pauseOffset: Long = 0

    private val calendar = Calendar.getInstance()
    private var marchDuration: Int = 0 // czas marszu w minutach

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Widoki
        val nameInput = findViewById<EditText>(R.id.name_input)
        val spinner = findViewById<Spinner>(R.id.spinner)
        raceIcon = findViewById(R.id.race_icon)
        dateTimeText = findViewById(R.id.date_time_text)
        val dateButton = findViewById<Button>(R.id.pick_date_button)
        val timeButton = findViewById<Button>(R.id.pick_time_button)
        val elfSwitch = findViewById<Switch>(R.id.elf_switch)
        val cloakCheck = findViewById<CheckBox>(R.id.cloak_checkbox)
        val lembasCheck = findViewById<CheckBox>(R.id.lembas_checkbox)
        val torchCheck = findViewById<CheckBox>(R.id.torch_checkbox)
        val priorityGroup = findViewById<RadioGroup>(R.id.priority_group)
        val seekBar = findViewById<SeekBar>(R.id.march_seekbar)
        val durationText = findViewById<TextView>(R.id.duration_text)
        chronometer = findViewById(R.id.chronometer)
        val chronoStart = findViewById<Button>(R.id.start_chrono)
        val chronoStop = findViewById<Button>(R.id.stop_chrono)
        val countdownButton = findViewById<Button>(R.id.start_countdown)
        timerProgressBar = findViewById(R.id.progress_bar)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        summaryText = findViewById(R.id.summary_text)

        // Spinner setup
        val raceNames = listOf("Wybierz rasę bohatera", "hobbit", "człowiek", "elf", "krasnolud", "czarodziej")
        val raceIcons = listOf(0, R.drawable.hobbit_icon, R.drawable.human_icon, R.drawable.elf_icon, R.drawable.krasnolod_icon, R.drawable.wizard_icon)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, raceNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    raceIcon.setImageResource(raceIcons[position])
                } else {
                    raceIcon.setImageDrawable(null)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Date & Time Pickers
        dateButton.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                updateDateTimeText()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        timeButton.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, m)
                updateDateTimeText()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // SeekBar
        seekBar.max = 300
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                marchDuration = progress
                durationText.text = "Czas marszu: $marchDuration min"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })


        // Chronometer START
        chronoStart.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
        }

        // Chronometer STOP
        chronoStop.setOnClickListener {
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
        }
        val chronoReset = findViewById<Button>(R.id.reset_chrono)
        chronoReset.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            chronometer.stop()
        }


        // CountDown Timer
        countdownButton.setOnClickListener {
            timerProgressBar.progress = 0
            timerProgressBar.max = 30

            object : CountDownTimer(30000, 1000) {
                var counter = 0
                override fun onTick(millisUntilFinished: Long) {
                    counter++
                    timerProgressBar.progress = counter
                }
                override fun onFinish() {
                    Toast.makeText(this@MainActivity, "Czas wyruszyć z Rivendell!", Toast.LENGTH_LONG).show()
                }
            }.start()
        }

        // Podsumowanie
        val summaryButton = findViewById<Button>(R.id.show_summary_button)
        summaryButton.setOnClickListener {
            val name = nameInput.text.toString()
            val race = if (spinner.selectedItemPosition != 0) raceNames[spinner.selectedItemPosition] else "?"
            val priorityId = priorityGroup.checkedRadioButtonId
            val priority = findViewById<RadioButton>(priorityId)?.text ?: "?"
            val items = mutableListOf<String>()
            if (cloakCheck.isChecked) items.add("Płaszcz elfów")
            if (lembasCheck.isChecked) items.add("Lembasy")
            if (torchCheck.isChecked) items.add("Pochodnia")
            val morale = ratingBar.rating.toInt()
            val formatter = SimpleDateFormat("dd.MM.yyyy 'o' HH:mm", Locale.getDefault())
            val dateTime = formatter.format(calendar.time)

            summaryText.text = """
                Bohater: $name ($race)
                Priorytet: $priority
                Wyposażenie: ${items.joinToString(", ")}
                Czas marszu: $marchDuration min • Morale: $morale/5
                Termin: $dateTime
                ${if (elfSwitch.isChecked) "Tajne szlaki elfów: Włączone" else ""}
            """.trimIndent()
        }
    }

    private fun updateDateTimeText() {
        val formatter = SimpleDateFormat("dd.MM.yyyy 'o' HH:mm", Locale.getDefault())
        dateTimeText.text = "Wyruszasz: ${formatter.format(calendar.time)}"
    }
}
