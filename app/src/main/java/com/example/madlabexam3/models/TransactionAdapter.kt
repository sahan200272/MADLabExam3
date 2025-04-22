package com.example.madlabexam3.models

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabexam3.R
import com.example.madlabexam3.models.Transaction

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<MyViewHolder>() {

    // Create and return a new ViewHolder (which will be used to display an item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_view, parent, false)
        return MyViewHolder(itemView)
    }

    // Bind data to the ViewHolder (populate the views with data)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set data to views
        holder.tvTitle.text = transaction.title
        holder.tvAmount.text = "$${transaction.amount}"
        holder.tvCategory.text = transaction.category.toString()
        holder.tvDate.text = transaction.date

//        // Optionally set up click listeners for Edit and Delete buttons
//        holder.editButton.setOnClickListener {
//            // Handle Edit button click, e.g., open Edit Activity
//        }
//
//        holder.deleteButton.setOnClickListener {
//            // Handle Delete button click, e.g., delete the transaction
//        }
    }

    // Return the size of the data list
    override fun getItemCount(): Int {
        return transactions.size
    }
}
