package com.example.madlabexam3

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madlabexam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import java.util.Calendar

class EditTransaction : AppCompatActivity() {

    private lateinit var tvAutoComplete: AutoCompleteTextView
    private lateinit var tvDate: EditText
    private lateinit var btnDate: Button
    private lateinit var radioGroupForTransaction: RadioGroup
    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnUpdateTransaction: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private var calendar = Calendar.getInstance()
    private var transactionPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_transaction)

        // Initialize views
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        tvDate = findViewById(R.id.etDate)
        btnDate = findViewById(R.id.btnDatePicker)
        radioGroupForTransaction = findViewById(R.id.radioGroupForTransaction)
        tvAutoComplete = findViewById(R.id.tvAutoComplete)
        btnUpdateTransaction = findViewById(R.id.btnUpdateTransaction)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle Window Insets for Edge to Edge mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the transaction data from intent
        val title = intent.getStringExtra("title")
        val amount = intent.getStringExtra("amount")
        val category = intent.getStringExtra("category")
        val date = intent.getStringExtra("date")
        val type = intent.getStringExtra("type")
        transactionPosition = intent.getIntExtra("position", -1)

        // Pre-fill the form with existing data
        etTitle.setText(title)
        etAmount.setText(amount)
        tvAutoComplete.setText(category)
        tvDate.setText(date)

        // Set the radio button based on transaction type
        if (type == "income") {
            radioGroupForTransaction.check(R.id.radioBtnIncome)
        } else {
            radioGroupForTransaction.check(R.id.radioBtnExpense)
        }

        // Set up Date Picker button click listener
        btnDate.setOnClickListener {
            showDatePicker()
        }

        // Set up AutoCompleteTextView for category selection
        val items = resources.getStringArray(R.array.category_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        tvAutoComplete.setAdapter(adapter)

        // Set up bottom navigation
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

        // Handle Update Transaction button click
        btnUpdateTransaction.setOnClickListener {
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

            val newTitle = etTitle.text.toString()
            val newAmount = etAmount.text.toString()
            val newCategory = tvAutoComplete.text.toString()
            val newDate = tvDate.text.toString()

            if (newTitle.isEmpty() || newAmount.isEmpty() || newCategory.isEmpty() || newDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // Update the transaction in SharedPreferences
                updateTransaction(transactionPosition, newTitle, newAmount.toFloat(), newCategory, newDate, value)

                // Return to TransactionManagement activity
                val intent = Intent(this, TransactionManagement::class.java)
                startActivity(intent)
                finish()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

    private fun updateTransaction(position: Int, title: String, amount: Float, category: String, date: String, type: String) {
        val sharedPref = getSharedPreferences("TransactionData", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()

        // Retrieve the existing list of transactions
        val existingTransactionsJson = sharedPref.getString("transactions_list", null)
        val existingTransactions: MutableList<Transaction> = if (existingTransactionsJson != null) {
            gson.fromJson(existingTransactionsJson, Array<Transaction>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        // Update the transaction at the specified position
        if (position in existingTransactions.indices) {
            // Get the old transaction to calculate the difference
            val oldTransaction = existingTransactions[position]
            val oldAmount = oldTransaction.amount
            val oldType = if (oldAmount >= 0) "income" else "expense"

            // Update the transaction with proper sign for expense
            val newAmount = if (type == "expense") -amount else amount
            existingTransactions[position] = Transaction(title, newAmount, category, date)
            
            // Update the list in SharedPreferences
            val updatedJson = gson.toJson(existingTransactions)
            editor.putString("transactions_list", updatedJson)

            // Get current totals
            val previousIncome = sharedPref.getFloat("totalIncome", 0.0f)
            val previousExpense = sharedPref.getFloat("totalExpense", 0.0f)

            // Calculate new totals
            val newIncome = if (oldType == "income") {
                if (type == "income") previousIncome - oldAmount + amount
                else previousIncome - oldAmount
            } else {
                if (type == "income") previousIncome + amount
                else previousIncome
            }

            val newExpense = if (oldType == "expense") {
                if (type == "expense") previousExpense - Math.abs(oldAmount) + amount
                else previousExpense - Math.abs(oldAmount)
            } else {
                if (type == "expense") previousExpense + amount
                else previousExpense
            }

            // Update totals
            editor.putFloat("totalIncome", newIncome)
            editor.putFloat("totalExpense", newExpense)
            editor.putFloat("totalBalance", newIncome - newExpense)

            editor.apply()
        }
    }
} 