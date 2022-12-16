package com.example.imageslider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.makeramen.roundedimageview.RoundedImageView
import mx.arquidiocesis.eamxprofilemodule.R

class SliderAdapter internal constructor(
    sliderItems: MutableList<SliderItem>,
    view: ViewPager2
):RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){

    private val sliderItems: List<SliderItem>
    init {
        this.sliderItems = sliderItems
    }

    class SliderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imageView: RoundedImageView = itemView.findViewById(R.id.sliderImagesLayout)

        fun image(sliderItem: SliderItem){
            imageView.setImageResource(sliderItem.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slider_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.image(sliderItems[position])
    }
}