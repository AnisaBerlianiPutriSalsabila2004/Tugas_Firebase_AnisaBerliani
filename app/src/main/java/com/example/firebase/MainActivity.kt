package com.example.firebase

import Budget
import Pengaduan
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityMainBinding
import com.example.pertemuan_android_room.ui.dataAdapter
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("pengaduan")
    private lateinit var binding: ActivityMainBinding
    private var updateId = ""
    private val budgetListLiveData: MutableLiveData<List<Pengaduan>> by lazy {
        MutableLiveData<List<Pengaduan>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeBudgets()
        getAllBudgets()

        with(binding) {
            btnadd.setOnClickListener{
                val intentToInput = Intent(this@MainActivity, InputActivity::class.java)
                intentToInput.putExtra("COMMAND", "ADD")
                startActivity(intentToInput)
            }
        }
    }

    private fun getAllBudgets() {
        observeBudgetChanges()
    }
    private fun observeBudgets() {
        budgetListLiveData.observe(this) { pengaduan ->
            if (pengaduan.isNotEmpty()) { // Periksa apakah daftar catatan tidak kosong
                binding.rvnotes.isVisible = true
                binding.textEmpty.isVisible = false
                val recyclerAdapter = dataAdapter(pengaduan)
                binding.rvnotes.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    setHasFixedSize(true)
                    adapter = recyclerAdapter
                }
            }else{
                binding.rvnotes.isVisible = false
                binding.textEmpty.isVisible = true
            }
        }
    }
    private fun observeBudgetChanges() {
        budgetCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val pengaduan = snapshots?.toObjects(Pengaduan::class.java)
            if (pengaduan != null) {
                budgetListLiveData.postValue(pengaduan)
            }
        }
    }
    private fun addBudget(budget: Budget) {
        budgetCollectionRef.add(budget)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                budget.id = createdBudgetId
                documentReference.set(budget)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating budget ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding budget: ", it)
            }
    }
    private fun updateBudget(budget: Budget) {
        budget.id = updateId
        budgetCollectionRef.document(updateId).set(budget)
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating budget: ", it)
            }
    }
    private fun deleteBudget(budget: Budget) {
        if (budget.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: budget ID is empty!")
            return
        }
        budgetCollectionRef.document(budget.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting budget: ", it)
            }
    }
}
