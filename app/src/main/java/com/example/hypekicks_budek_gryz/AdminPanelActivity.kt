package com.example.hypekicks_budek_gryz

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val db = FirebaseFirestore.getInstance()

    private val sneakers = mutableListOf<String>()
    private val sneakerIds = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        listView = findViewById(R.id.adminListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sneakers)
        listView.adapter = adapter

        val brandInput = findViewById<EditText>(R.id.brandInput)
        val modelInput = findViewById<EditText>(R.id.modelInput)
        val priceInput = findViewById<EditText>(R.id.priceInput)
        val yearInput = findViewById<EditText>(R.id.yearInput)
        val imageUrlInput = findViewById<EditText>(R.id.imageUrlInput)
        val addButton = findViewById<Button>(R.id.addButton)

        loadSneakers()

        addButton.setOnClickListener {
            val brand = brandInput.text.toString()
            val model = modelInput.text.toString()
            val price = priceInput.text.toString().toFloatOrNull() ?: 0f
            val year = yearInput.text.toString()
            val imageUrl = imageUrlInput.text.toString()

            if (brand.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Uzupełnij markę i model!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sneaker = hashMapOf(
                "brand" to brand,
                "modelName" to model,
                "resellPrice" to price,
                "releaseYear" to year,
                "imageUrl" to imageUrl
            )

            db.collection("sneakers")
                .add(sneaker)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dodano do bazy!", Toast.LENGTH_SHORT).show()
                    loadSneakers()
                }
                .addOnFailureListener { e ->
                    Log.e("ADMIN_ERROR", "Błąd dodawania", e)
                    Toast.makeText(this, "Błąd dodawania!", Toast.LENGTH_SHORT).show()
                }
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val docId = sneakerIds[position]

            db.collection("sneakers")
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Usunięto z bazy!", Toast.LENGTH_SHORT).show()
                    loadSneakers()
                }
                .addOnFailureListener { e ->
                    Log.e("ADMIN_ERROR", "Błąd usuwania", e)
                    Toast.makeText(this, "Błąd podczas usuwania!", Toast.LENGTH_SHORT).show()
                }

            true
        }
    }

    private fun loadSneakers() {
        db.collection("sneakers")
            .get()
            .addOnSuccessListener { result ->
                sneakers.clear()
                sneakerIds.clear()

                for (doc in result) {
                    val brand = doc.getString("brand") ?: ""
                    val model = doc.getString("modelName") ?: ""

                    sneakers.add("$brand - $model")
                    sneakerIds.add(doc.id)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ADMIN_ERROR", "Błąd pobierania", e)
            }
    }
}
