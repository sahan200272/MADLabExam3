package com.example.madlabexam3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class EditDeleteController : Fragment() {

    private lateinit var btnEdit:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.transaction_view, container, false)

        btnEdit = rootView.findViewById(R.id.btnEdit)

        btnEdit.setOnClickListener {


        }


        return rootView
    }

}