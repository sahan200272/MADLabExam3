package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BudgetDetails : AppCompatActivity() {

    private lateinit var btnBudgetSetup: Button
    private lateinit var etBudget:EditText
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //loadSavedData()

        btnBudgetSetup = findViewById(R.id.btnBudgetSetup)
        etBudget = findViewById(R.id.etBudget)

        sharedPreferences = getSharedPreferences("Budget", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        loadSavedData()

        val budgetValueString = intent.getStringExtra("budgetAmount")

        if(!budgetValueString.isNullOrEmpty()){

            val budgetValue = budgetValueString.toFloat()

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

    @SuppressLint("SetTextI18n")
    private fun loadSavedData(){

        etBudget.setText(sharedPreferences.getFloat("budget", 0.0f).toString())
    }
}
