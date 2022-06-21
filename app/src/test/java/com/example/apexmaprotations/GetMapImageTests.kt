package com.example.apexmaprotations

import com.example.apexmaprotations.hilt.RetroFitModule
import com.example.apexmaprotations.repo.ApexRepo
import com.example.apexmaprotations.retrofit.ApexStatusApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GetMapImageTests {
    private lateinit var apexApi: ApexStatusApi
    private lateinit var repo: ApexRepo

    @Before
    fun getApexApiAndRepo() {
        apexApi = RetroFitModule.provideRetrofitInstance()
        repo = ApexRepo(apexApi)
    }

    @Test
    fun `getKingsCanyonImage returns correct image`() = kotlin.run {
        //  store any incorrect images
        val wrongImages: MutableList<Int> = mutableListOf()
        for (i in 0..100) {
            val img = repo.getKingsCanyonImg()
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
            val img = repo.getOlympusImg()
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
            val img = repo.getStormPointImg()
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
            val img = repo.getWorldsEdgeImg()
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