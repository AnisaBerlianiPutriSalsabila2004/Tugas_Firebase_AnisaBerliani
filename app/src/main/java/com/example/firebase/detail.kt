package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebase.databinding.ActivityDetailBinding
import com.example.firebase.databinding.ActivityInputBinding

class detail : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var item_nama = intent.getStringExtra("NAMA")
        var item_pesan = intent.getStringExtra("PESAN")
        var item_date = intent.getStringExtra("TANGGAL")

        with(binding){
            nama.text = "Nama Pengirim :  ${item_nama}"
            tanggal.text = "Tanggal Aduan :  ${item_date}"
            pesan.text = "Pesan Aduan :  ${item_pesan}"

            btnKembali.setOnClickListener{
                val IntentToHome = Intent(this@detail, MainActivity::class.java)
                startActivity(IntentToHome)
            }
        }
    }
}