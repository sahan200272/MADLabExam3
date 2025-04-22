package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class BudgetDetails : AppCompatActivity() {

    private lateinit var btnBudgetSetup: Button
    private lateinit var etBudget:EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var progressBarBudget:ProgressBar

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

        sharedPreferences = getSharedPreferences("Budget", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        loadSavedData()

        val budgetValueString = intent.getStringExtra("budgetAmount")

        if(!budgetValueString.isNullOrEmpty()){

            val budgetValue = budgetValueString.toFloat()
            val totalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

            val progress = (totalExpense / budgetValue) * 100

            progressBarBudget.progress = progress.toInt()

            if (progress >= 80) {
                showBudgetAlert()
            }

            editor.putFloat("budget", budgetValue)
            editor.apply()

            loadSavedData()
        }

        btnBudgetSetup.setOnClickListener {

            val newFragment = MonthlyBudgetSetup()
            val trans = supportFragmentManager.beginTransaction()
            trans.replace(R.id.main, newFragment)
            trans.commit()
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
    private fun loadSavedData(){

        val budgetValue = sharedPreferences.getFloat("budget", 0.0f)
        val totalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

        val progress = (totalExpense / budgetValue) * 100
        progressBarBudget.progress = progress.toInt()

        if (progress >= 80) {
            showBudgetAlert()
        }

        etBudget.setText(sharedPreferences.getFloat("budget", 0.0f).toString())
    }
}
