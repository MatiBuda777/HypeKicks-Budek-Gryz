package com.example.hypekicks_budek_gryz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hypekicks_budek_gryz.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var allSneakersList: MutableList<Sneaker>
    lateinit var sneakersList: MutableList<Sneaker>
    lateinit var adapter: SneakerAdapter
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //seedDataBase()

        allSneakersList = mutableListOf()
        sneakersList = mutableListOf()
        adapter = SneakerAdapter(this, sneakersList)
        binding.sneakersGridView.adapter = adapter

        fetchDataFromCloud()

        binding.sneakersGridView.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = sneakersList[position]
            Toast.makeText(this, "But ${clickedItem.modelName}!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun seedDataBase() {
        val sneakers = listOf(
            Sneaker(
                brand = "Nike",
                modelName = "Air Jordan 1 Retro High OG 'Chicago'",
                resellPrice = 1800f,
                releaseYear = "2015",
                imageUrl = ""
            ),
            Sneaker(
                brand = "Adidas",
                modelName = "Yeezy Boost 350 V2 'Zebra'",
                resellPrice = 1200f,
                releaseYear = "2017",
                imageUrl = ""
            ),
            Sneaker(
                brand = "New Balance",
                modelName = "990v3 'Grey'",
                resellPrice = 900f,
                releaseYear = "2012",
                imageUrl = ""
            ),
            Sneaker(
                brand = "Puma",
                modelName = "MB.01 'Red Blast'",
                resellPrice = 700f,
                releaseYear = "2021",
                imageUrl = ""
            ),
            Sneaker(
                brand = "Reebok",
                modelName = "Question Mid 'Red Toe'",
                resellPrice = 850f,
                releaseYear = "1996",
                imageUrl = ""
            )
        )


        val db = Firebase.firestore

        for (sneaker in sneakers) {
            db.collection("sneakers")
                .add(sneaker)
                .addOnSuccessListener {
                    Log.d("FIREBASE_TEST", "Dodano buta: ${sneaker.}")
                }
                .addOnFailureListener { e ->
                    Log.e("FIREBASE_TEST", "Błąd podczas dodawania: ", e)
                }
        }
    }


    private fun fetchDataFromCloud() {
        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                sneakersList.clear()

                for (document in documents) {
                    val brand = document.getString("brand") ?: "Nieznana marka"
                    val modelName = document.getString("modelName") ?: "Nieznany model"
                    val resellPrice = document.getLong("resellPrice")?.toFloat() ?: 0F
                    val releaseYear = document.getString("releaseYear") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""

                    val sneaker = Sneaker(brand, modelName, resellPrice, releaseYear, imageUrl)
                    sneakersList.add(sneaker)
                }

                // MAGIA: Mówimy Adapterowi "Hej, mam nowe dane! Odśwież ekran!"
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { ex ->
                Log.e("FIREBASE_ERROR", "Błąd pobierania danych: ", ex)
                Toast.makeText(this, "Błąd pobierania danych z chmury!", Toast.LENGTH_LONG).show()
            }
    }
}
