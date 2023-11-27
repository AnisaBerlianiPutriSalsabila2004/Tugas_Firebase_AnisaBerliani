package com.example.pertemuan_android_room.ui

import Budget
import Pengaduan
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.InputActivity
import com.example.firebase.R
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.detail
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class dataAdapter(val dataPengaduan: List<Pengaduan>?): RecyclerView.Adapter<dataAdapter.MyViewHolder>() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("pengaduan")
    private lateinit var binding: ActivityMainBinding
    private var updateId = ""
    private val budgetListLiveData: MutableLiveData<List<Pengaduan>> by lazy {
        MutableLiveData<List<Pengaduan>>()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tittle = view.findViewById<TextView>(R.id.name_txt)
//        val desc = view.findViewById<TextView>(R.id.message_txt)
//        val date = view.findViewById<TextView>(R.id.date_txt)
        val btnUpdate = view.findViewById<Button>(R.id.btn_update)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rvnotes, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (dataPengaduan != null) {
            return dataPengaduan.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tittle.text = "Nama Pengirim : ${dataPengaduan?.get(position)?.nama}"
//        holder.desc.text = "Pesan Pengaduan : ${dataPengaduan?.get(position)?.pesan}"
//        holder.date.text = "Tanggal : ${dataPengaduan?.get(position)?.tanggal}"

        holder.itemView.setOnClickListener {
            val intentToDetail = Intent(holder.itemView.context, detail::class.java)
            intentToDetail.putExtra("NAMA", dataPengaduan?.get(position)?.nama)
            intentToDetail.putExtra("PESAN", dataPengaduan?.get(position)?.pesan)
            intentToDetail.putExtra("TANGGAL", dataPengaduan?.get(position)?.tanggal)
            holder.itemView.context.startActivity(intentToDetail)
        }


        holder.btnUpdate.setOnClickListener {
            val intentToDetail = Intent(holder.itemView.context, InputActivity::class.java)
            intentToDetail.putExtra("ID", dataPengaduan?.get(position)?.id)
            intentToDetail.putExtra("NAMA", dataPengaduan?.get(position)?.nama)
            intentToDetail.putExtra("PESAN", dataPengaduan?.get(position)?.pesan)
            intentToDetail.putExtra("TANGGAL", dataPengaduan?.get(position)?.tanggal)
            intentToDetail.putExtra("COMMAND", "UPDATE")
            holder.itemView.context.startActivity(intentToDetail)
        }

        holder.btnDelete.setOnClickListener {
            val noteId = dataPengaduan?.get(position)?.id
            if (noteId != null) {
                budgetCollectionRef.document(noteId).delete()
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error deleting budget: ", it)
                    }
            }
            true
        }
    }
}
