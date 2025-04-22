package com.example.madlabexam3

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MonthlyBudgetSetup : Fragment() {

    private lateinit var btnSaveBudget:Button
    private lateinit var etBudget:EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_monthly_budget_setup, container, false)

        btnSaveBudget = rootView.findViewById(R.id.btnSaveBudget)
        etBudget = rootView.findViewById(R.id.etBudget)

        btnSaveBudget.setOnClickListener {

            val budgetAmount = etBudget.text.toString()

            if (budgetAmount.isNotEmpty()) {

                Toast.makeText(activity, "Budget saved: $budgetAmount", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, BudgetDetails::class.java)
                intent.putExtra("budgetAmount", budgetAmount)
                startActivity(intent)

            } else {
                Toast.makeText(activity, "Please enter a valid budget", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }
}