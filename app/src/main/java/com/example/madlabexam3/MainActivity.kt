package com.example.madlabexam3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var btnAddExpenses: Button
    lateinit var tvTotalAmount: TextView
    lateinit var tvTotalExpenseAmount: TextView
    lateinit var tvFoodAmount: TextView
    lateinit var tvTransportAmount: TextView
    lateinit var tvBillAmount: TextView
    lateinit var tvEntertainmentAmount: TextView
    lateinit var tvEducationAmount: TextView

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTotalExpenseAmount = findViewById(R.id.tvTotalExpenseAmount)
        tvFoodAmount = findViewById(R.id.tvFoodAmount)
        tvTransportAmount = findViewById(R.id.tvTransportAmount)
        tvBillAmount = findViewById(R.id.tvBillAmount)
        tvEntertainmentAmount = findViewById(R.id.tvEntertainmentAmount)
        tvEducationAmount = findViewById(R.id.tvEducationAmount)
        btnAddExpenses = findViewById(R.id.btnAddExpenses)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Load previously saved data
        loadSavedData()

        // Get data from intent if any
        val selectedRadioBtn = intent.getStringExtra("selectedRadioBtn")
        val amountString = intent.getStringExtra("amount")
        val dropDown = intent.getStringExtra("dropDown")

        if (!amountString.isNullOrEmpty()) {
            val amount = amountString.toInt()

            if (selectedRadioBtn == "income") {
                val savedIncome = sharedPreferences.getInt("income", 0)
                val newIncome = savedIncome + amount
                editor.putInt("income", newIncome)
                editor.apply()
                tvTotalAmount.text = newIncome.toString()

            } else {
                val savedExpense = sharedPreferences.getInt("expense", 0)
                val newExpense = savedExpense + amount
                editor.putInt("expense", newExpense)

                // Category update
                when (dropDown) {
                    "Food" -> {
                        val prev = sharedPreferences.getInt("food", 0)
                        val total = prev + amount
                        editor.putInt("food", total)
                    }
                    "Transport" -> {
                        val prev = sharedPreferences.getInt("transport", 0)
                        val total = prev + amount
                        editor.putInt("transport", total)
                    }
                    "Bill" -> {
                        val prev = sharedPreferences.getInt("bill", 0)
                        val total = prev + amount
                        editor.putInt("bill", total)
                    }
                    "Entertainment" -> {
                        val prev = sharedPreferences.getInt("entertainment", 0)
                        val total = prev + amount
                        editor.putInt("entertainment", total)
                    }
                    "Education" -> {
                        val prev = sharedPreferences.getInt("education", 0)
                        val total = prev + amount
                        editor.putInt("education", total)
                    }
                    else -> Toast.makeText(this, "Invalid category", Toast.LENGTH_SHORT).show()
                }

                editor.apply()
                loadSavedData() // refresh the UI
            }
        }

        btnAddExpenses.setOnClickListener {
            val intent = Intent(this, AddTransaction::class.java)
            startActivity(intent)
        }
    }

    private fun loadSavedData() {
        tvTotalAmount.text = sharedPreferences.getInt("income", 0).toString()
        tvTotalExpenseAmount.text = sharedPreferences.getInt("expense", 0).toString()
        tvFoodAmount.text = sharedPreferences.getInt("food", 0).toString()
        tvTransportAmount.text = sharedPreferences.getInt("transport", 0).toString()
        tvBillAmount.text = sharedPreferences.getInt("bill", 0).toString()
        tvEntertainmentAmount.text = sharedPreferences.getInt("entertainment", 0).toString()
        tvEducationAmount.text = sharedPreferences.getInt("education", 0).toString()
    }
}
