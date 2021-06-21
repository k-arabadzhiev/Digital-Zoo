package com.pollux.digitalzoo.features.animals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.databinding.AnimalCardBinding
import com.pollux.digitalzoo.util.C

class AnimalAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Animal, AnimalAdapter.AnimalViewHolder>(AnimalComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val binding = AnimalCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class AnimalViewHolder(private val binding: AnimalCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(binding.animalImageCard, item)
                    }
                }
            }
        }

        fun bind(animal: Animal) {
            val photoUrl =
                if (animal.animalPhoto.contains("http"))
                    animal.animalPhoto
                else
                    C.GET_PHOTO + animal.animalPhoto
            //Log.i("photo", "bind: $photoUrl")
            binding.apply {
                animalImageCard.transitionName = "transition_${animal.animalName}"
                //Log.i("ADAPTER", "${animal.animalName} transition set to ... ")
                Glide.with(itemView)
                    .load(photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(animalImage)
                animalName.text = animal.animalName
                animalSpecies.text = animal.species
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(cardView: View, animal: Animal)
    }

    class AnimalComparator : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Animal, newItem: Animal) =
            oldItem == newItem
    }
}