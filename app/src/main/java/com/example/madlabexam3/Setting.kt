package com.example.madlabexam3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class Setting : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("TransactionData", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Find the backup layout and set click listener
        val backupLayout = findViewById<LinearLayout>(R.id.linearLayoutBackup)
        backupLayout?.setOnClickListener {
            backupData()
        }

        // Find the restore layout and set click listener
        val restoreLayout = findViewById<LinearLayout>(R.id.linearLayoutRestore)
        restoreLayout?.setOnClickListener {
            showRestoreDialog()
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

    private fun backupData() {
        try {
            // Get all data from SharedPreferences
            val totalIncome = sharedPreferences.getFloat("totalIncome", 0.0f)
            val totalExpense = sharedPreferences.getFloat("totalExpense", 0.0f)
            val totalBalance = sharedPreferences.getFloat("totalBalance", 0.0f)
            val transactionsJson = sharedPreferences.getString("transactions_list", "[]")

            // Create backup data string
            val backupData = """
                {
                    "totalIncome": $totalIncome,
                    "totalExpense": $totalExpense,
                    "totalBalance": $totalBalance,
                    "transactions": $transactionsJson
                }
            """.trimIndent()

            // Create backup directory if it doesn't exist
            val backupDir = File(filesDir, "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }

            // Create backup file with timestamp
            val timestamp = System.currentTimeMillis()
            val backupFile = File(backupDir, "backup_$timestamp.json")

            // Write backup data to file
            FileOutputStream(backupFile).use { outputStream ->
                outputStream.write(backupData.toByteArray())
            }

            Toast.makeText(this, "Backup created successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRestoreDialog() {
        val backupDir = File(filesDir, "backups")
        if (!backupDir.exists() || backupDir.listFiles()?.isEmpty() == true) {
            Toast.makeText(this, "No backup files found", Toast.LENGTH_SHORT).show()
            return
        }

        val backupFiles = backupDir.listFiles()?.sortedByDescending { it.lastModified() }
        val fileNames = backupFiles?.map { it.name }?.toTypedArray() ?: emptyArray()

        AlertDialog.Builder(this)
            .setTitle("Select Backup to Restore")
            .setItems(fileNames) { _, which ->
                backupFiles?.get(which)?.let { restoreFromBackup(it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun restoreFromBackup(backupFile: File) {
        try {
            // Read backup file
            val jsonString = FileInputStream(backupFile).bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            // Restore data to SharedPreferences
            editor.putFloat("totalIncome", jsonObject.getDouble("totalIncome").toFloat())
            editor.putFloat("totalExpense", jsonObject.getDouble("totalExpense").toFloat())
            editor.putFloat("totalBalance", jsonObject.getDouble("totalBalance").toFloat())
            editor.putString("transactions_list", jsonObject.getString("transactions"))
            editor.apply()

            Toast.makeText(this, "Backup restored successfully!", Toast.LENGTH_SHORT).show()
            
            // Restart the app to reflect changes
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}