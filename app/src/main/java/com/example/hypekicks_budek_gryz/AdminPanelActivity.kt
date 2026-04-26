package com.example.hypekicks_budek_gryz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val db = FirebaseFirestore.getInstance()

    private val sneakers = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        listView = findViewById(R.id.adminListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sneakers)
        listView.adapter = adapter

        loadSneakers()
    }

    private fun loadSneakers() {
        db.collection("sneakers")
            .get()
            .addOnSuccessListener { result ->
                sneakers.clear()

                for (doc in result) {
                    val brand = doc.getString("brand") ?: ""
                    val model = doc.getString("modelName") ?: ""
                    sneakers.add("$brand - $model")
                }

                adapter.notifyDataSetChanged()
            }
    }
}
