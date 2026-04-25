package com.example.hypekicks_budek_gryz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.hypekicks_budek_gryz.databinding.ItemSneakerBinding

class SneakerAdapter (
    private val context: Context,
    private val sneakerList: List<Sneaker>
) : BaseAdapter() {

    override fun getCount(): Int = sneakerList.size

    override fun getItem(position: Int): Any = sneakerList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemSneakerBinding
        val view: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = ItemSneakerBinding.inflate(inflater, parent, false)
            view = binding.root

            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSneakerBinding
        }

        val sneaker = sneakerList[position]

        binding.itemBrand.text = sneaker.brand
        binding.itemModel.text = sneaker.modelName

        Glide.with(context)
            .load(sneaker.imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .into(binding.itemImageView)

        return view
    }
}