package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madlabexam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var etTotalBalance:EditText
    private lateinit var etTotalIncome:EditText
    private lateinit var etTotalExpense:EditText
    private lateinit var btnDelete:Button

    //create shared preferences variable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    //bottom navigation variable
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

        //initialize variables
        etTotalBalance = findViewById(R.id.etTotalBalance)
        etTotalIncome = findViewById(R.id.etTotalIncome)
        etTotalExpense = findViewById(R.id.etTotalExpense)

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        loadSaveData();

        val selectedRadioBtn = intent.getStringExtra("selectedRadioBtn")
        val titleGet = intent.getStringExtra("title")
        val amountString = intent.getStringExtra("amount")
        val categoryGet = intent.getStringExtra("category")
        val dateGet = intent.getStringExtra("date")

        if(!amountString.isNullOrEmpty()){

            val amount = amountString.toFloat()
            val title = titleGet.toString()
            val category = categoryGet.toString()
            val date = dateGet.toString()

            val transaction = Transaction(title, amount, category, date)
            saveTransaction(this, transaction)

            if(selectedRadioBtn == "income"){

                val previousIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
                val prevTotalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

                val totalIncome = previousIncome + amount
                val totalBalance = totalIncome - prevTotalExpense

                editor.putFloat("totalIncome", totalIncome)
                editor.putFloat("totalBalance", totalBalance)
                editor.putString("title", title)
                editor.putString("category", category)
                editor.putString("date", date)

                editor.apply()
            }
            else{

                val previousExpense = sharedPreferences.getFloat("totalExpense", 0.0f)
                val prevTotalIncome = sharedPreferences.getFloat("totalIncome", 0.0f)

                val totalExpense = previousExpense + amount
                val totalBalance = prevTotalIncome - totalExpense

                editor.putFloat("totalExpense", totalExpense)
                editor.putFloat("totalBalance", totalBalance)
                editor.putString("title", title)
                editor.putString("category", category)
                editor.putString("date", date)

                editor.apply()
            }

            editor.apply()
            loadSaveData()
        }

        btnDelete = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener{
            editor.clear()
            editor.apply()
            loadSaveData()
        }

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
    }

    @SuppressLint("SetTextI18n")
    private fun loadSaveData() {

        etTotalIncome.setText(sharedPreferences.getFloat("totalIncome", 0.0f).toString())
        etTotalExpense.setText(sharedPreferences.getFloat("totalExpense", 0.0f).toString())
        etTotalBalance.setText(sharedPreferences.getFloat("totalBalance", 0.0f).toString())
    }

    private fun saveTransaction(context: Context, transaction: Transaction) {
        val sharedPref = context.getSharedPreferences("TransactionData", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()

        // Retrieve the existing list of transactions, if available
        val existingTransactionsJson = sharedPref.getString("transactions_list", null)
        val existingTransactions: MutableList<Transaction> = if (existingTransactionsJson != null) {
            gson.fromJson(existingTransactionsJson, Array<Transaction>::class.java).toMutableList()
        } else {
            mutableListOf()
        }

        // Add the new transaction to the list
        existingTransactions.add(transaction)

        // Save the updated list back to SharedPreferences
        val updatedJson = gson.toJson(existingTransactions)
        editor.putString("transactions_list", updatedJson)
        editor.apply()
    }

}
