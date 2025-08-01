package com.example.madlabexam3

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class AddTransaction : AppCompatActivity() {

    // Declare views as lateinit
    private lateinit var tvAutoComplete: AutoCompleteTextView
    private lateinit var tvDate: EditText
    private lateinit var btnDate: Button
    private lateinit var radioGroupForTransaction: RadioGroup
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnAddTransaction: Button

    private var calendar = Calendar.getInstance()

    private lateinit var bottomNavigationView:BottomNavigationView

    // Function onCreate()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)

        // Initialize views after setContentView() is called
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        tvDate = findViewById(R.id.etDate)
        btnDate = findViewById(R.id.btnDatePicker)
        radioGroupForTransaction = findViewById(R.id.radioGroupForTransaction)
        tvAutoComplete = findViewById(R.id.tvAutoComplete)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle Window Insets for Edge to Edge mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up Date Picker button click listener
        btnDate.setOnClickListener {
            showDatePicker()
        }

        // Set up AutoCompleteTextView for category selection
        val items = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        tvAutoComplete.setAdapter(adapter)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_transaction -> {
                    val intent = Intent(this, TransactionManagement::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_budget -> {
                    val intent = Intent(this, BudgetDetails::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, Setting::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Handle Add Transaction button click
        btnAddTransaction.setOnClickListener {

            // Get selected radio button value
            val selectedRadioButtonId = radioGroupForTransaction.checkedRadioButtonId
            var value = ""

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                value = selectedRadioButton.text.toString()

            } else {
                Toast.makeText(this, "Please select a transaction type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = etTitle.text.toString()
            val amount = etAmount.text.toString()
            val category = tvAutoComplete.text.toString()
            val date = tvDate.text.toString()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selectedRadioBtn", value)
            intent.putExtra("title", title)
            intent.putExtra("amount", amount)
            intent.putExtra("category", category)
            intent.putExtra("date", date)

            startActivity(intent)
        }
    }

    // Function to display DatePicker dialog
    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val dateString = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                tvDate.setText(dateString)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
