package com.example.madlabexam3.models

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabexam3.EditTransaction
import com.example.madlabexam3.R
import com.example.madlabexam3.models.Transaction
import com.google.gson.Gson

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.MyViewHolder>() {

    private val mutableTransactions = transactions.toMutableList()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    // Create and return a new ViewHolder (which will be used to display an item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_view, parent, false)
        return MyViewHolder(itemView)
    }

    // Bind data to the ViewHolder (populate the views with data)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaction = mutableTransactions[position]

        // Set data to views
        holder.tvTitle.text = transaction.title
        holder.tvAmount.text = "$${transaction.amount}"
        holder.tvCategory.text = transaction.category
        holder.tvDate.text = transaction.date

        // Set up click listener for Edit button
        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditTransaction::class.java)
            intent.putExtra("title", transaction.title)
            intent.putExtra("amount", transaction.amount.toString())
            intent.putExtra("category", transaction.category)
            intent.putExtra("date", transaction.date)
            intent.putExtra("position", position)
            // Determine if it's income or expense based on the amount
            intent.putExtra("type", if (transaction.amount >= 0) "income" else "expense")
            context.startActivity(intent)
        }

        // Set up click listener for Delete button
        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val sharedPref = context.getSharedPreferences("TransactionData", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val gson = Gson()

            // Get the transaction to be deleted
            val deletedTransaction = mutableTransactions[position]
            val deletedAmount = deletedTransaction.amount
            val isIncome = deletedAmount >= 0

            // Remove the transaction from the list
            mutableTransactions.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, mutableTransactions.size)

            // Update SharedPreferences
            val updatedJson = gson.toJson(mutableTransactions)
            editor.putString("transactions_list", updatedJson)

            // Update total income/expense
            val previousIncome = sharedPref.getFloat("totalIncome", 0.0f)
            val previousExpense = sharedPref.getFloat("totalExpense", 0.0f)

            if (isIncome) {
                editor.putFloat("totalIncome", previousIncome - deletedAmount)
            } else {
                editor.putFloat("totalExpense", previousExpense - deletedAmount)
            }

            // Update total balance
            val totalIncome = if (isIncome) previousIncome - deletedAmount else previousIncome
            val totalExpense = if (!isIncome) previousExpense - deletedAmount else previousExpense
            editor.putFloat("totalBalance", totalIncome - totalExpense)

            editor.apply()
            Toast.makeText(context, "Transaction deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

    // Return the size of the data list
    override fun getItemCount(): Int {
        return mutableTransactions.size
    }
}
