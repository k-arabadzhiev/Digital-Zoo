package com.pollux.digitalzoo.features.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pollux.digitalzoo.MainActivity
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.databinding.AnimalSearchFragmentBinding
import com.pollux.digitalzoo.databinding.AnimalSearchItemBinding
import com.pollux.digitalzoo.features.animals.AnimalViewModel
import com.pollux.digitalzoo.features.user.ZookeeperViewModel
import com.pollux.digitalzoo.util.onQueryTextChange
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AnimalSearchFragment"

@AndroidEntryPoint
class AnimalSearchFragment : Fragment(R.layout.animal_search_fragment),
    SearchAdapter.OnItemClickListener {

    private lateinit var searchView: SearchView

    private val viewModel: AnimalViewModel by activityViewModels()
    private val zookeeperViewModel: ZookeeperViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).supportActionBar?.title = "Search for an animal"

        val binding = AnimalSearchFragmentBinding.bind(view)

        val searchAdapter = SearchAdapter(this, zookeeperViewModel.loginState)

        Log.i(TAG, "onViewCreated: $viewModel $zookeeperViewModel")
        
        binding.apply {
            animalsList.apply {
                adapter = searchAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            ViewCompat.setNestedScrollingEnabled(animalsList, false)
        }

        viewModel.animalByName.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it)
        }

        setHasOptionsMenu(true)
    }

    override fun onEditClick(binding: AnimalSearchItemBinding, animal: Animal) {
        val action =
            AnimalSearchFragmentDirections.actionAnimalSearchFragmentToAddEditAnimalFragment(
                "Edit Animal",
                animal
            )
        findNavController().navigate(action)
    }

    override fun onItemClick(binding: AnimalSearchItemBinding, animal: Animal) {
        val action =
            AnimalSearchFragmentDirections.actionAnimalSearchFragmentToAnimalDetailsFragment(
                animal.animalName,
                animal
            )
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.animal_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChange { query ->
            viewModel.searchQuery.value = query
        }
    }

}