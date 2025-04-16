package com.example.madlabexam3.models

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)
