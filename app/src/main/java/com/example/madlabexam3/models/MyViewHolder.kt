package com.example.madlabexam3.models

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madlabexam3.R

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
    val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    val tvDate: TextView = itemView.findViewById(R.id.tvDate)

}
