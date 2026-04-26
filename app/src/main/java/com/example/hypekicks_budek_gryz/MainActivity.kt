package com.example.hypekicks_budek_gryz

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.SearchView
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

//        seedDataBase()

        allSneakersList = mutableListOf()
        sneakersList = mutableListOf()
        adapter = SneakerAdapter(this, sneakersList)
        binding.sneakersGridView.adapter = adapter


        fetchDataFromCloud()

        binding.sneakersGridView.setOnItemClickListener { _, _, position, _ ->
            val clickedItem = sneakersList[position]
            Toast.makeText(this, "But ${clickedItem.modelName}!", Toast.LENGTH_SHORT).show()
        }

        binding.sneakersSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterSneakers(newText ?: "")
                return true
            }
        })

    }

    private fun seedDataBase() {
        val sneakers = listOf(
            Sneaker(
                brand = "Nike",
                modelName = "Air Force",
                resellPrice = 1800f,
                releaseYear = "2015",
                imageUrl = "https://i.postimg.cc/dVWR6RCv/AIR-FORCE-1-07.png"
            ),
            Sneaker(
                brand = "Adidas",
                modelName = "Yeezy Boost",
                resellPrice = 1200f,
                releaseYear = "2017",
                imageUrl = "https://i.postimg.cc/JhGN2y3h/Adidas-Yeezy-Boost-350-V2-Zebra-Product-1.png"
            ),
            Sneaker(
                brand = "New Balance",
                modelName = "990v3 'Grey'",
                resellPrice = 900f,
                releaseYear = "2012",
                imageUrl = "https://i.postimg.cc/0NbYB6Y4/1653919886289-New-Balance0.png"
            ),
            Sneaker(
                brand = "Puma",
                modelName = "MB.01 'Red Blast'",
                resellPrice = 700f,
                releaseYear = "2021",
                imageUrl = "https://i.postimg.cc/jjmH0svC/puma-red-blast.png"
            ),
            Sneaker(
                brand = "Reebok",
                modelName = "Question Mid 'Red Toe'",
                resellPrice = 850f,
                releaseYear = "1996",
                imageUrl = "https://i.postimg.cc/HLRwDp33/reebok-redtoe.png"
            )
        )


        val db = Firebase.firestore

        for (sneaker in sneakers) {
            db.collection("sneakers")
                .add(sneaker)
                .addOnSuccessListener {
                    Log.d("FIREBASE_TEST", "Dodano buta: ${sneaker.modelName}")
                }
                .addOnFailureListener { e ->
                    Log.e("FIREBASE_TEST", "Błąd podczas dodawania: ${sneaker.modelName}", e)
                }
        }
    }


    private fun fetchDataFromCloud() {
        db.collection("sneakers")
            .get()
            .addOnSuccessListener { documents ->
                allSneakersList.clear()
                sneakersList.clear()

                for (document in documents) {
                    val brand = document.getString("brand") ?: "Nieznana marka"
                    val modelName = document.getString("modelName") ?: "Nieznany model"
                    val resellPrice = document.getLong("resellPrice")?.toFloat() ?: 0F
                    val releaseYear = document.getString("releaseYear") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""

                    val sneaker = Sneaker(brand, modelName, resellPrice, releaseYear, imageUrl)
                    allSneakersList.add(sneaker)
                }
                sneakersList.addAll(allSneakersList)

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { ex ->
                Log.e("FIREBASE_ERROR", "Błąd pobierania danych: ", ex)
                Toast.makeText(this, "Błąd pobierania danych z chmury!", Toast.LENGTH_LONG).show()
            }
    }

    private fun filterSneakers(query: String){
        sneakersList.clear()

        if (query.isEmpty()) {
            sneakersList.addAll(allSneakersList)
        } else {
            for (sneaker in allSneakersList) {
                if (sneaker.brand.lowercase().contains(query.lowercase())) {
                    sneakersList.add(sneaker)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}
