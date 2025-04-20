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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etTotalBalance:EditText
    private lateinit var etTotalIncome:EditText
    private lateinit var etTotalExpense:EditText
    private lateinit var btnAddExpenses: Button
    private lateinit var btnDelete:Button

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

        etTotalBalance = findViewById(R.id.etTotalBalance)
        etTotalIncome = findViewById(R.id.etTotalIncome)
        etTotalExpense = findViewById(R.id.etTotalExpense)

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        loadSaveData();

        var selectedRadioBtn = intent.getStringExtra("selectedRadioBtn")
        var amountString = intent.getStringExtra("amount")

        if(!amountString.isNullOrEmpty()){

            val amount = amountString.toFloat()

            if(selectedRadioBtn == "income"){

                var previousIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
                var prevTotalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

                var totalIncome = previousIncome + amount
                var totalBalance = totalIncome - prevTotalExpense

                editor.putFloat("totalIncome", totalIncome)
                editor.putFloat("totalBalance", totalBalance)
                editor.apply()
            }
            else{

                var previousExpense = sharedPreferences.getFloat("totalExpense", 0.0f)
                var prevTotalIncome = sharedPreferences.getFloat("totalIncome", 0.0f)

                var totalExpense = previousExpense + amount
                var totalBalance = prevTotalIncome - totalExpense

                editor.putFloat("totalExpense", totalExpense)
                editor.putFloat("totalBalance", totalBalance)
                editor.apply()
            }

            editor.apply()
            loadSaveData()
        }

        btnAddExpenses = findViewById(R.id.btnAddExpenses)
        btnAddExpenses.setOnClickListener {
            val intent = Intent(this, AddTransaction::class.java)
            startActivity(intent)
        }

        btnDelete = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener{
            editor.clear()
            editor.apply()
            loadSaveData()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSaveData() {

        etTotalIncome.setText(sharedPreferences.getFloat("totalIncome", 0.0f).toString())
        etTotalExpense.setText(sharedPreferences.getFloat("totalExpense", 0.0f).toString())
        etTotalBalance.setText(sharedPreferences.getFloat("totalBalance", 0.0f).toString())
    }
}
