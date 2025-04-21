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
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var etTotalBalance:EditText
    private lateinit var etTotalIncome:EditText
    private lateinit var etTotalExpense:EditText
    private lateinit var btnAddExpenses: Button
    private lateinit var btnDelete:Button

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var bottomNavigationView: BottomNavigationView

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

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        loadSaveData();

        val selectedRadioBtn = intent.getStringExtra("selectedRadioBtn")
        val amountString = intent.getStringExtra("amount")

        if(!amountString.isNullOrEmpty()){

            val amount = amountString.toFloat()

            if(selectedRadioBtn == "income"){

                val previousIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
                val prevTotalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

                val totalIncome = previousIncome + amount
                val totalBalance = totalIncome - prevTotalExpense

                editor.putFloat("totalIncome", totalIncome)
                editor.putFloat("totalBalance", totalBalance)
                editor.apply()
            }
            else{

                val previousExpense = sharedPreferences.getFloat("totalExpense", 0.0f)
                val prevTotalIncome = sharedPreferences.getFloat("totalIncome", 0.0f)

                val totalExpense = previousExpense + amount
                val totalBalance = prevTotalIncome - totalExpense

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

        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
//                R.id.nav_transaction -> {
//                    val intent = Intent(this, AddTransaction::class.java)
//                    startActivity(intent)
//                    true
//                }
                R.id.nav_budget -> {
                    val intent = Intent(this, BudgetDetails::class.java)
                    startActivity(intent)
                    true
                }
//                R.id.nav_settings -> {
//                    val intent = Intent(this, Settings::class.java)
//                    startActivity(intent)
//                    true
//                }
                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSaveData() {

        etTotalIncome.setText(sharedPreferences.getFloat("totalIncome", 0.0f).toString())
        etTotalExpense.setText(sharedPreferences.getFloat("totalExpense", 0.0f).toString())
        etTotalBalance.setText(sharedPreferences.getFloat("totalBalance", 0.0f).toString())
    }
}
