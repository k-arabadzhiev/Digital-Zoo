package com.pollux.digitalzoo.features.user

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pollux.digitalzoo.R
import com.pollux.digitalzoo.data.Animal
import com.pollux.digitalzoo.databinding.AddEditAnimalFragmentBinding
import com.pollux.digitalzoo.util.C
import com.pollux.digitalzoo.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddEditAnimalFragment"

@AndroidEntryPoint
class AddEditAnimalFragment : Fragment(R.layout.add_edit_animal_fragment) {

    private val zookeeperViewModel: ZookeeperViewModel by activityViewModels()

    private var _binding: AddEditAnimalFragmentBinding? = null
    private val binding get() = _binding!!

    private var fileUri: Uri = Uri.EMPTY
    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null)
            fileUri = uri
        Log.i("Uri", fileUri.path.toString())
    }
    private val args by navArgs<AddEditAnimalFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = findNavController()

        Log.i(TAG, "onCreate: ${zookeeperViewModel.loginState}")
        if (!zookeeperViewModel.loginState) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.animalsFragment, false)
                .build()
            navController.navigate(
                R.id.loginFragment,
                null,
                navOptions
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddEditAnimalFragmentBinding.bind(view)

        zookeeperViewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                showSnackbar(it)
                zookeeperViewModel.onSnackbarShown()
            }
        }

        binding.apply {
            if (args.animal == null) {
                addUpdateAnimal(this, true)
            } else {
                addUpdateAnimal(this, false)
            }
        }
    }

    private fun addUpdateAnimal(binding: AddEditAnimalFragmentBinding, add: Boolean) {
        val speciesAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.species_list_item,
                C.speciesValues.subList(1, C.speciesValues.size)
            )
        val speciesTextView = (binding.species.editText as? AutoCompleteTextView)
        speciesTextView?.setAdapter(speciesAdapter)

        var speciesSelected: String? = null
        speciesTextView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val item = parent?.getItemAtPosition(position)
                speciesSelected = item.toString()
            }

        val dietAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.diet_list_item,
                C.dietTypes.subList(1, C.dietTypes.size)
            )
        val dietTextView = (binding.diet.editText as? AutoCompleteTextView)
        dietTextView?.setAdapter(dietAdapter)
        var dietSelected: String? = null
        dietTextView?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val item = parent?.getItemAtPosition(position)
                dietSelected = item.toString()
            }

        if (!add) {
            binding.animalNameId.setText(args.animal?.animalName)
            binding.animalAgeId.setText(args.animal?.age)
            binding.animalFoodId.setText(args.animal?.food)
            binding.animalWeightId.setText(args.animal?.weight.toString())
            binding.animalInfoId.setText(args.animal?.info)
            binding.animalHabitatId.setText(args.animal?.habitat)
        }

        val animalNameOld = args.animal?.animalName
        if (add)
            binding.addUpdateAnimal.text = getString(R.string.add_animal_button)
        else
            binding.addUpdateAnimal.text = getString(R.string.edit_animal_button)

        binding.addUpdateAnimal.setOnClickListener {
            val name = binding.animalNameId.text.toString().trim()
            val age = binding.animalAgeId.text.toString().trim()
            val food = binding.animalFoodId.text.toString().trim()
            val weight = binding.animalWeightId.text.toString()
            val info = binding.animalInfoId.text.toString().trim()
            val habitat = binding.animalHabitatId.text.toString().trim()

            if (checkEmptyFields(
                    binding = binding, name, age, food, weight, info,
                    habitat, speciesSelected, dietSelected
                )
            ) {
                val newAnimal = Animal(
                    animalName = name,
                    animalPhoto = "",
                    animalNameOld = animalNameOld,
                    zookeeper = zookeeperViewModel.zookeeper,
                    age = age,
                    diet = dietSelected!!,
                    food = food,
                    info = info,
                    species = speciesSelected!!,
                    weight = weight,
                    habitat = habitat
                )
                if (fileUri != Uri.EMPTY) {
                    val inputStream = context?.contentResolver?.openInputStream(fileUri)!!
                    zookeeperViewModel.addOrUpdateAnimal(newAnimal, inputStream, add)
                } else {
                    if (add)
                        showSnackbar("Choose a photo!")
                    else
                        zookeeperViewModel.addOrUpdateAnimal(newAnimal, null, add)
                }
                //findNavController().popBackStack()
            }
        }
        binding.photoUploadButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    /*private suspend fun loadPhotoFromExternalStorage(): List<StoragePhoto> {
        return withContext(Dispatchers.IO) {
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            )

            val photos = mutableListOf<StoragePhoto>()

            resolver.resolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(StoragePhoto(id, displayName, contentUri))
                }
                photos.toList()
            } ?: listOf()

        }
    }*/

    private fun checkEmptyFields(
        binding: AddEditAnimalFragmentBinding,
        name: String, age: String, food: String,
        weight: String, info: String, habitat: String,
        speciesSelected: String?,
        dietSelected: String?
    ): Boolean {
        if (name.isEmpty()) {
            binding.animalNameId.error = "Name is empty!"
            return false
        } else binding.animalNameId.error = null
        if (age.isEmpty()) {
            binding.animalAgeId.error = "Age is empty!"
            return false
        } else binding.animalAgeId.error = null
        if (food.isEmpty()) {
            binding.animalFoodId.error = "Food is empty!"
            return false
        } else binding.animalFoodId.error = null
        if (weight.isEmpty()) {
            binding.animalWeightId.error = "Weight is empty!"
            return false
        } else binding.animalWeightId.error = null
        if (info.isEmpty()) {
            binding.animalInfoId.error = "Information is empty!"
            return false
        } else binding.animalInfoId.error = null
        if (habitat.isEmpty()) {
            binding.animalHabitatId.error = "Habitat is empty!"
            return false
        } else binding.animalHabitatId.error = null
        if (speciesSelected.isNullOrEmpty()) {
            binding.species.error = "Species is empty!"
            return false
        } else binding.species.error = null
        if (dietSelected.isNullOrEmpty()) {
            binding.diet.error = "Diet is empty!"
            return false
        } else binding.diet.error = null
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

