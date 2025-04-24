package com.example.madlabexam3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabexam3.models.Transaction
import com.example.madlabexam3.models.TransactionAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class TransactionManagement : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnAddTransaction: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: List<Transaction>

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_management)

        // Set up edge-to-edge UI handling for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupClickListeners()
        setupRecyclerView()
    }

    private fun initializeViews() {
        btnAddTransaction = findViewById(R.id.btnAddTransaction)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        recyclerView = findViewById(R.id.recyclerView)
        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun setupClickListeners() {
        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransaction::class.java)
            startActivity(intent)
        }

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

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        dataList = getTransactionsFromSharedPreferences(this)
        val adapter = TransactionAdapter(dataList.toMutableList())
        recyclerView.adapter = adapter
    }

    private fun getTransactionsFromSharedPreferences(context: Context): List<Transaction> {
        val sharedPref = context.getSharedPreferences("TransactionData", Context.MODE_PRIVATE)
        val gson = Gson()
        val transactionsJson = sharedPref.getString("transactions_list", null)

        return if (transactionsJson != null) {
            gson.fromJson(transactionsJson, Array<Transaction>::class.java).toList()
        } else {
            emptyList()
        }
    }
}
