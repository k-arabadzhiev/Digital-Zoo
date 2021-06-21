package com.pollux.digitalzoo.features.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.databinding.AnimalSearchItemBinding
import com.pollux.digitalzoo.util.C

class SearchAdapter(
    private val listener: OnItemClickListener, private var enableEditButton: Boolean = false
) :
    ListAdapter<Animal, SearchAdapter.SearchViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = AnimalSearchItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class SearchViewHolder(private val binding: AnimalSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(binding, item)
                    }
                }
            }
            binding.editButton.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val item = getItem(bindingAdapterPosition)
                    if (item != null) {
                        listener.onEditClick(binding, item)
                    }
                }
            }
        }

        fun bind(animal: Animal) {
            binding.apply {
                animalName.text = animal.animalName
                animalSpecies.text = animal.species
                val photoUrl =
                    if (animal.animalPhoto.contains("http"))
                        animal.animalPhoto
                    else
                        C.GET_PHOTO + animal.animalPhoto
                Glide.with(itemView)
                    .load(photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(animalPhoto)
                editButton.isVisible = enableEditButton
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(binding: AnimalSearchItemBinding, animal: Animal)
        fun onEditClick(binding: AnimalSearchItemBinding, animal: Animal)
    }

    class DiffCallback : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Animal, newItem: Animal) =
            oldItem == newItem
    }

}