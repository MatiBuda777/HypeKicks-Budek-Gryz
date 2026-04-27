package com.example.hypekicks_budek_gryz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hypekicks_budek_gryz.databinding.ActivitySneakerDetailsBinding

class SneakerDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySneakerDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySneakerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val brand = intent.getStringExtra("brand")
        val model = intent.getStringExtra("model")
        val imageUrl = intent.getStringExtra("imageUrl")
        val price = intent.getStringExtra("price")

        binding.detailsBrand.text = brand
        binding.detailsModel.text = model
        binding.detailsPrice.text = "$price zł"

        Glide.with(this)
            .load(imageUrl)
            .into(binding.detailsImage)
    }
}
