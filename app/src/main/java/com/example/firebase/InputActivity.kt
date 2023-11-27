package com.example.firebase

import Pengaduan
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.example.firebase.databinding.ActivityInputBinding
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class InputActivity : AppCompatActivity() {
    private lateinit var binding : ActivityInputBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("pengaduan")
    private var updateId = ""
    private val budgetListLiveData: MutableLiveData<List<Pengaduan>> by lazy {
        MutableLiveData<List<Pengaduan>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInputBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        var command = intent.getStringExtra("COMMAND")


        with(binding){

            if(command=="UPDATE"){
                binding.btnUpdate.isVisible = true
                binding.btnAdd.isVisible = false
                updateId = intent.getStringExtra("ID").toString()
                var item_nama = intent.getStringExtra("NAMA")
                var item_pesan = intent.getStringExtra("PESAN")
                var item_date = intent.getStringExtra("TANGGAL")

                binding.txtNama.setText(item_nama.toString())
                binding.txtDate.setText(item_date.toString())
                binding.txtPesan.setText(item_pesan.
                toString())
            }else{
                binding.btnUpdate.isVisible = false
                binding.btnAdd.isVisible = true
            }

            btnAdd.setOnClickListener {
                if (validateInput()) {
                    val namas = txtNama.text.toString()
                    val pesans = txtPesan.text.toString()
                    val tanggals = txtDate.text.toString()
                    val newBudget = Pengaduan(
                        nama = namas, pesan = pesans,
                        tanggal = tanggals
                    )
                    addBudget(newBudget)
                    val IntentToHome = Intent(this@InputActivity, MainActivity::class.java)
                    Toast.makeText(this@InputActivity, "Berhasil Menambahkan Data", Toast.LENGTH_SHORT).show()
                    startActivity(IntentToHome)
                }else{
                    Toast.makeText(this@InputActivity, "Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
                }
            }

            btnUpdate.setOnClickListener {
                if (validateInput()) {
                val namas = txtNama.text.toString()
                val pesans = txtPesan.text.toString()
                val tanggals = txtDate.text.toString()
                val budgetToUpdate = Pengaduan(nama = namas, pesan = pesans,
                    tanggal = tanggals)
                updateBudget(budgetToUpdate)
                updateId = ""
                setEmptyField()
                val IntentToHome = Intent(this@InputActivity, MainActivity::class.java)
                Toast.makeText(this@InputActivity, "Berhasil Mengupdate Data", Toast.LENGTH_SHORT).show()
                startActivity(IntentToHome)
            }else{
            Toast.makeText(this@InputActivity, "Kolom Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()
        }
            }

        }
    }

    private fun addBudget(pengaduan: Pengaduan) {
        budgetCollectionRef.add(pengaduan)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                pengaduan.id = createdBudgetId
                documentReference.set(pengaduan)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating budget ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding budget: ", it)
            }
    }

    private fun updateBudget(pengaduan: Pengaduan) {
        pengaduan.id = updateId
        budgetCollectionRef.document(updateId).set(pengaduan)
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating budget: ", it)
            }
    }

    private fun setEmptyField() {
        with(binding) {
            txtNama.setText("")
            txtDate.setText("")
            txtPesan.setText("")
        }
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if(txtDate.text.toString()!="" && txtNama.text.toString()!="" && txtPesan.text.toString()!=""){
                return true
            }else{
                return false
            }
        }

    }
}