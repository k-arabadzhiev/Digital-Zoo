package com.pollux.digitalzoo.features.animals

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialElevationScale
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.databinding.AnimalsFragmentBinding
import com.pollux.digitalzoo.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimalsFragment : Fragment(R.layout.animals_fragment), AnimalAdapter.OnItemClickListener {

    private val viewModel: AnimalViewModel by viewModels()
    private var _binding: AnimalsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = AnimalsFragmentBinding.bind(view)

        val animalAdapter = AnimalAdapter(this)

        viewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                showSnackbar(it) { snack ->
                    snack.anchorView = binding.snackbarAnchor
                }
                viewModel.onSnackbarShown()
            }
        }

        binding.apply {
            recyclerView.apply {
                adapter = animalAdapter
                postponeEnterTransition()
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }

            viewModel.progressBarVisibility.observe(viewLifecycleOwner){
                progressBar.isVisible = it
            }

            viewModel.animalsFlow.observe(viewLifecycleOwner) {
                animalAdapter.submitList(it)
            }
            chipGroupSpecies.setOnCheckedChangeListener { _, checkedId ->
                viewModel.speciesChipSelected(checkedId)
            }
            chipGroupDiet.setOnCheckedChangeListener { _, checkedId ->
                viewModel.dietChipSelected(checkedId)
            }
        }
    }

    override fun onItemClick(cardView: View, animal: Animal) {

        exitTransition = MaterialElevationScale(false).apply {
            duration = 400L
        }

        reenterTransition = MaterialElevationScale(true).apply {
            duration = 400L
        }
        val animalCardTransitionName = "transition_animal_detail"
        val extras = FragmentNavigatorExtras(
            cardView to animalCardTransitionName
        )
        val action = AnimalsFragmentDirections
            .actionAnimalsFragmentToAnimalDetailsFragment(animal.animalName, animal)
        findNavController().navigate(action, extras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}