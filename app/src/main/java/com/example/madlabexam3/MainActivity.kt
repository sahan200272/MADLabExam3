package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.madlabexam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var tvTotalBalance:TextView
    private lateinit var tvTotalIncome:TextView
    private lateinit var tvTotalExpense:TextView
    private lateinit var btnDelete:Button

    private lateinit var cvFood:CardView
    private lateinit var cvTransport:CardView
    private lateinit var cvBill:CardView
    private lateinit var cvEntertainment:CardView
    private lateinit var cvEducation:CardView

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
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)

        //Card views initialize
        cvFood = findViewById(R.id.cvFood)
        cvTransport = findViewById(R.id.cvTransport)
        cvBill = findViewById(R.id.cvBill)
        cvEntertainment = findViewById(R.id.cvEntertainment)
        cvEducation = findViewById(R.id.cvEducation)

        val cardViews = listOf(cvFood, cvTransport, cvBill, cvEntertainment, cvEducation)

        //Set on click listener for each category go to the relevant screen
        cardViews.forEach{cardView ->
            run {
                cardView.setOnClickListener {
                    when (cardView.id) {
                        R.id.cvFood -> {
                            val intent = Intent(this, CategorySummary::class.java)
                            intent.putExtra("selectedCategory", "Food")
                            startActivity(intent)
                        }
                        R.id.cvTransport -> {
                            val intent = Intent(this, CategorySummary::class.java)
                            intent.putExtra("selectedCategory", "Transport")
                            startActivity(intent)
                        }
                        R.id.cvBill -> {
                            val intent = Intent(this, CategorySummary::class.java)
                            intent.putExtra("selectedCategory", "Bill")
                            startActivity(intent)
                        }
                        R.id.cvEntertainment -> {
                            val intent = Intent(this, CategorySummary::class.java)
                            intent.putExtra("selectedCategory", "Entertainment")
                            startActivity(intent)
                        }
                        R.id.cvEducation -> {
                            val intent = Intent(this, CategorySummary::class.java)
                            intent.putExtra("selectedCategory", "Education")
                            startActivity(intent)
                        }
                    }
                }
            }
        }

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

            // Store expense as negative amount
            val transactionAmount = if (selectedRadioBtn == "expense") -amount else amount
            val transaction = Transaction(title, transactionAmount, category, date)
            saveTransaction(this, transaction)

            val previousIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
            val previousExpense = sharedPreferences.getFloat("totalExpense", 0.0f)

            if(selectedRadioBtn == "income"){
                val totalIncome = previousIncome + amount
                val totalBalance = totalIncome - previousExpense

                editor.putFloat("totalIncome", totalIncome)
                editor.putFloat("totalBalance", totalBalance)
            }
            else{
                val totalExpense = previousExpense + amount
                val totalBalance = previousIncome - totalExpense

                editor.putFloat("totalExpense", totalExpense)
                editor.putFloat("totalBalance", totalBalance)
            }

            editor.putString("title", title)
            editor.putString("category", category)
            editor.putString("date", date)
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

        tvTotalIncome.text = sharedPreferences.getFloat("totalIncome", 0.0f).toString()
        tvTotalExpense.text = sharedPreferences.getFloat("totalExpense", 0.0f).toString()
        tvTotalBalance.text = sharedPreferences.getFloat("totalBalance", 0.0f).toString()
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
