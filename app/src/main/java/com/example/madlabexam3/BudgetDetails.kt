package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator

class BudgetDetails : AppCompatActivity() {

    private lateinit var btnBudgetSetup: Button
    private lateinit var etBudget: EditText
    private lateinit var budgetSharedPreferences: SharedPreferences
    private lateinit var transactionSharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var progressBarBudget: CircularProgressIndicator
    private lateinit var tvProgressPercentage: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBudgetSetup = findViewById(R.id.btnBudgetSetup)
        etBudget = findViewById(R.id.etBudget)
        progressBarBudget = findViewById(R.id.progressBarBudget)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)

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

        budgetSharedPreferences = getSharedPreferences("Budget", MODE_PRIVATE)
        transactionSharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = budgetSharedPreferences.edit()

        loadSavedData()

        val budgetValueString = intent.getStringExtra("budgetAmount")

        if(!budgetValueString.isNullOrEmpty()){
            try {
                val budgetValue = budgetValueString.toFloat()
                editor.putFloat("budget", budgetValue)
                editor.apply()
                updateProgress(budgetValue)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show()
            }
        }

        btnBudgetSetup.setOnClickListener {
            val budgetAmount = etBudget.text.toString()
            if (budgetAmount.isNotEmpty()) {
                try {
                    val budgetValue = budgetAmount.toFloat()
                    editor.putFloat("budget", budgetValue)
                    editor.apply()
                    updateProgress(budgetValue)
                    Toast.makeText(this, "Budget updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showBudgetAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Budget Alert")
        builder.setMessage("You are about to exceed your monthly budget. Would you like to review your expenses?")
        builder.setPositiveButton("Review") { dialog, which ->
            openExpenseReviewActivity()
        }
        builder.setNegativeButton("Dismiss") { dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun openExpenseReviewActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun loadSavedData() {
        val budgetValue = budgetSharedPreferences.getFloat("budget", 0.0f)
        etBudget.setText(budgetValue.toString())
        updateProgress(budgetValue)
    }

    private fun updateProgress(budgetValue: Float) {
        if (budgetValue > 0) {
            val totalExpense = transactionSharedPreferences.getFloat("totalExpense", 0.0f)
            val progress = (totalExpense / budgetValue) * 100
            val progressInt = progress.toInt().coerceIn(0, 100)
            
            progressBarBudget.progress = progressInt
            tvProgressPercentage.text = "$progressInt%"

            if (progress >= 80) {
                showBudgetAlert()
            }
        } else {
            progressBarBudget.progress = 0
            tvProgressPercentage.text = "0%"
        }
    }
}
