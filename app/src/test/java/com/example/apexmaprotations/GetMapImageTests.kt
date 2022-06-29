package com.example.apexmaprotations

import com.example.apexmaprotations.repo.ApexRepoImpl
import com.example.apexmaprotations.repo.FakeApexRepo
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetMapImageTests {
    private lateinit var repo: ApexRepoImpl

    @Before
    fun setup() {
        repo = FakeApexRepo()
    }

    @Test
    fun `getKingsCanyonImage returns correct image`() = kotlin.run {
        //  store any incorrect images
        val wrongImages: MutableList<Int> = mutableListOf()
        for (i in 0..100) {
            val img = repo.getKingsCanyonImage()
            if (img != R.drawable.kings_canyon_1 &&
                img != R.drawable.kings_canyon_2 &&
                img != R.drawable.kings_canyon_3
            ) {
                wrongImages.add(img)
            }
        }
        val wrongImageFound = wrongImages.isNotEmpty()
        Assert.assertEquals(false, wrongImageFound)
    }

    @Test
    fun `getOlympusImage returns correct image`() = kotlin.run {
        //  store any incorrect images
        val wrongImages: MutableList<Int> = mutableListOf()
        for (i in 0..100) {
            val img = repo.getOlympusImage()
            if (img != R.drawable.transition_olympus &&
                img != R.drawable.olympus_3 &&
                img != R.drawable.olympus_2 &&
                img != R.drawable.transition_olympus_mu1
            ) {
                wrongImages.add(img)
            }
        }
        val wrongImageFound = wrongImages.isNotEmpty()
        Assert.assertEquals(false, wrongImageFound)
    }

    @Test
    fun `getStormPointImage returns correct image`() = kotlin.run {
        //  store any incorrect images
        val wrongImages: MutableList<Int> = mutableListOf()
        for (i in 0..100) {
            val img = repo.getStormPointImage()
            if (img != R.drawable.storm_point_1 &&
                img != R.drawable.storm_point_2
            ) {
                wrongImages.add(img)
            }
        }
        val wrongImageFound = wrongImages.isNotEmpty()
        Assert.assertEquals(false, wrongImageFound)
    }

    @Test
    fun `getWorldsEdgeImage returns correct image`() = kotlin.run {
        //  store any incorrect images
        val wrongImages: MutableList<Int> = mutableListOf()
        for (i in 0..100) {
            val img = repo.getWorldsEdgeImage()
            if (img != R.drawable.worlds_edge_1 &&
                img != R.drawable.worlds_edge_2 &&
                img != R.drawable.worlds_edge_3
            ) {
                wrongImages.add(img)
            }
        }
        val wrongImageFound = wrongImages.isNotEmpty()
        Assert.assertEquals(false, wrongImageFound)
    }
}