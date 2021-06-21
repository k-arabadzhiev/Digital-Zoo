package com.pollux.digitalzoo.features.animals

import androidx.lifecycle.*
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.data.ZooRepository
import com.pollux.digitalzoo.util.C
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "AnimalViewModel"

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: ZooRepository
) : ViewModel() {

    private val _progressBarVisibility = MutableLiveData(false)
    val progressBarVisibility: LiveData<Boolean> = _progressBarVisibility

    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?> get() = _snackbar

    private val animalsFetched =
        repository.getOrFetchAnimals(
            onFetchSuccess = {
                _progressBarVisibility.value = false
            },
            onFetchFailed = {
                _progressBarVisibility.value = false
                _snackbar.value = "Network connection failed!"
            }
        ).map {
            it.data!!
        }

    init {
        _progressBarVisibility.value = true
    }

    val searchQuery = MutableStateFlow("")
    private val animalsSearchFlow = searchQuery.flatMapLatest { query ->
        repository.getAnimalByName(query)
    }
    val animalByName = animalsSearchFlow.asLiveData()

    private val species = MutableStateFlow(C.speciesValues[0])
    private val diet = MutableStateFlow(C.dietTypes[0])

    private val animalsFilteredFlow = combine(
        species,
        diet
    ) { species, diet ->
        Pair(species, diet)
    }.flatMapLatest { (species, diet) ->
        repository.getFilteredAnimals(species, diet)
    }

    val animalsFlow = combine(
        animalsFetched,
        animalsFilteredFlow
    ) { fetched, filtered ->
        Pair(fetched, filtered)
    }.mapLatest { (fetched, filtered) ->
        if (species.value == C.speciesValues[0] && diet.value == C.dietTypes[0]) {
            fetched
        } else
            filtered
    }.asLiveData()

    fun speciesChipSelected(checkedId: Int) {
        when (checkedId) {
            R.id.chip_all_species -> species.value = C.speciesValues[0]
            R.id.chip_amphibians -> species.value = C.speciesValues[1]
            R.id.chip_birds -> species.value = C.speciesValues[2]
            R.id.chip_fish -> species.value = C.speciesValues[3]
            R.id.chip_mammals -> species.value = C.speciesValues[4]
            R.id.chip_reptiles -> species.value = C.speciesValues[5]
            else -> species.value = C.speciesValues[0]
        }
    }

    fun dietChipSelected(checkedId: Int) {
        when (checkedId) {
            R.id.chip_all -> diet.value = C.dietTypes[0]
            R.id.chip_carnivores -> diet.value = C.dietTypes[1]
            R.id.chip_herbivores -> diet.value = C.dietTypes[2]
            R.id.chip_omnivores -> diet.value = C.dietTypes[3]
            else -> diet.value = C.dietTypes[0]
        }
    }

    fun onSnackbarShown() {
        _snackbar.value = null
    }

}