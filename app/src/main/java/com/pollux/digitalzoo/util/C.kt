package com.pollux.digitalzoo.util

import com.pollux.digitalzoo.BuildConfig

class C {
    companion object{
        const val BASE_URL = BuildConfig.SERVER_URL
        const val GET_PHOTO = BASE_URL + "animals/"
        const val OK = "200 OK"
        const val UNSUCCESSFUL = "UNSUCCESSFUL"
        val speciesValues = listOf(
            "All",
            "Amphibians", "Birds", "Fish", "Mammals", "Reptiles",
            "Arachnids", "Crustaceans", "Echinoderms", "Insects", "Mollusks", "Myriapoda"
        )
        val dietTypes = listOf("All", "Carnivores", "Herbivores", "Omnivores")
        const val COLLAPSING_TOOLBAR_TITLE = "Explore\nDigital Zoo"
        const val EMPTY_TITLE = ""
    }
}