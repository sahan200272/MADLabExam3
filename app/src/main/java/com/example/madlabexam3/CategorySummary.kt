package com.example.madlabexam3

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabexam3.models.Transaction
import com.example.madlabexam3.models.TransactionAdapter
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson

class CategorySummary : AppCompatActivity() {

    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var dataList: List<Transaction>
    private lateinit var filteredDataList: List<Transaction>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBarCategory: CircularProgressIndicator
    private lateinit var tvTitle: TextView
    private lateinit var tvProgress: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_summary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvTitle = findViewById(R.id.tvTitle)
        progressBarCategory = findViewById(R.id.progressBarCategory)
        tvProgress = findViewById(R.id.tvProgress)

        recyclerViewCategory = findViewById(R.id.recyclerViewCategory)
        recyclerViewCategory.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)

        val totalIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
        val totalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)
        val categoryWise = sharedPreferences.getString("category", "")

        dataList = getTransactionsFromSharedPreferences(this)

        val category = intent.getStringExtra("selectedCategory")

        // Set the title according to the category
        when (category) {
            "Food" -> tvTitle.text = "Food Summary"
            "Transport" -> tvTitle.text = "Transport Summary"
            "Bill" -> tvTitle.text = "Bill Summary"
            "Entertainment" -> tvTitle.text = "Entertainment Summary"
            "Education" -> tvTitle.text = "Education Summary"
        }

        // Filter data according to the selected category
        filteredDataList = dataList.filter { it.category == category }

        // Calculate category total and percentage
        val categoryTotal = filteredDataList.sumOf { Math.abs(it.amount).toDouble() }
        val percentage = if (totalExpense > 0) {
            // Ensure percentage doesn't exceed 100
            minOf((categoryTotal / totalExpense * 100).toInt(), 100)
        } else {
            0
        }

        // Update progress bar and text
        progressBarCategory.setProgressCompat(percentage, true)
        tvProgress.text = "$percentage%"

        // Set the filtered data to RecyclerView
        val adapterCategoryWise = TransactionAdapter(filteredDataList)
        recyclerViewCategory.adapter = adapterCategoryWise
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
