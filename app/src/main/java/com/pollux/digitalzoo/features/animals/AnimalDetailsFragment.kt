package com.pollux.digitalzoo.features.animals

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialContainerTransform
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.databinding.AnimalDetailsFragmentBinding
import com.pollux.digitalzoo.util.C

class AnimalDetailsFragment : Fragment(R.layout.animal_details_fragment) {

    private var _binding: AnimalDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<AnimalDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 500L
            scrimColor = Color.TRANSPARENT
            //setAllContainerColors(requireContext().themeColor())
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 400L
            scrimColor = Color.TRANSPARENT
            //setAllContainerColors(requireContext().themeColor())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AnimalDetailsFragmentBinding.bind(view)

        binding.apply {
            val animal = args.animal
            val photoUrl =
                if (animal.animalPhoto.contains("http"))
                    animal.animalPhoto
                else
                    C.GET_PHOTO + animal.animalPhoto

            //ViewCompat.setNestedScrollingEnabled(scrollView, false)

            animalImage.apply {
                Glide.with(this@AnimalDetailsFragment)
                    .load(photoUrl)
                    .centerCrop()
                    .into(this)
            }

            animalNameId.text = animal.animalName
            animalSpecies.text = animal.species
            animalDiet.text = animal.diet
            animalInfo.text = animal.info
            animalFood.text = animal.food
            animalAge.text = animal.age
            animalWeight.text = animal.weight
            animalHabitat.text = animal.habitat
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}